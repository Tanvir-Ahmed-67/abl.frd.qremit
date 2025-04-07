package abl.frd.qremit.converter.service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.NecItalyModel;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.NecItalyModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class NecItalyModelService {
    @Autowired
    NecItalyModelRepository necItalyModelRepository;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);

            Map<String, Object> necItalyData = csvToNecItalyModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<NecItalyModel> necItalyModels = (List<NecItalyModel>) necItalyData.get("necItalyDataModelList");

            if(necItalyData.containsKey("errorMessage")){
                resp.put("errorMessage", necItalyData.get("errorMessage"));
            }
            if(necItalyData.containsKey("errorCount") && ((Integer) necItalyData.get("errorCount") >= 1)){
                int errorCount = (Integer) necItalyData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(necItalyModels.size()!=0) {
                for(NecItalyModel necItalyModel : necItalyModels){
                    necItalyModel.setFileInfoModel(fileInfoModel);
                    necItalyModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(necItalyModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(necItalyModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setNecItalyModel(necItalyModels);
   
                // SAVING TO MySql Data Table
                try{
                    fileInfoModelRepository.save(fileInfoModel);                
                    resp.put("fileInfoModel", fileInfoModel);
                }catch(Exception e){
                    resp.put("errorMessage", e.getMessage());
                }
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> csvToNecItalyModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<NecItalyModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            int i = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = csvRecord.get(0).trim();   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, NecItalyModel.class, resp, fileExchangeCode, 0, 0);
            List<NecItalyModel> necItalyDataModelList = (List<NecItalyModel>) modelResp.get("modelList");
            List<ErrorDataModel> errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
            String duplicateMessage = modelResp.get("duplicateMessage").toString();
            int duplicateCount = (int) modelResp.get("duplicateCount");
            
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && necItalyDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("necItalyDataModelList", necItalyDataModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode){
        Map<String, Object> data = new HashMap<>();
        LocalDate date = CommonService.convertStringToLocalDate(csvRecord.get(4), "dd/MM/yyyy");
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", CommonService.convertLocalDateToString(date));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", csvRecord.get(17));
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(12));
        data.put("bankName", bankName);
        data.put("bankCode", csvRecord.get(9));
        data.put("branchName", csvRecord.get(10));
        data.put("branchCode", branchCode);
        data.put("draweeBranchName", csvRecord.get(13));
        data.put("draweeBranchCode", csvRecord.get(14));
        data.put("purposeOfRemittance", csvRecord.get(15));
        data.put("sourceOfIncome", csvRecord.get(16));
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }


}
