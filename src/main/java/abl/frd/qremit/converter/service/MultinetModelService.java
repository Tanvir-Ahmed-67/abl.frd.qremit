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
import abl.frd.qremit.converter.model.MultinetModel;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.MultinetModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class MultinetModelService {
    @Autowired
    MultinetModelRepository multinetModelRepository;
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
    CommonService commonService;
    @Autowired
    CustomQueryService customQueryService;
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

            Map<String, Object> multinetData = csvToMultinetModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<MultinetModel> multinetModels = (List<MultinetModel>) multinetData.get("multinetDataModelList");

            if(multinetData.containsKey("errorMessage")){
                resp.put("errorMessage", multinetData.get("errorMessage"));
            }
            if(multinetData.containsKey("errorCount") && ((Integer) multinetData.get("errorCount") >= 1)){
                int errorCount = (Integer) multinetData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(multinetModels.size()!=0) {
                for(MultinetModel multinetModel : multinetModels){
                    multinetModel.setFileInfoModel(fileInfoModel);
                    multinetModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(multinetModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(multinetModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setMultinetModel(multinetModels);
   
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

    public Map<String, Object> csvToMultinetModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<MultinetModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            int i = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (CSVRecord csvRecord : csvRecords) {
                if ( csvRecord.get(0).isEmpty())   continue;
                i++;
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = csvRecord.get(0).trim();   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, MultinetModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<MultinetModel> multinetDataModelList = (List<MultinetModel>) modelResp.get("modelList");
            errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
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
            if(errorDataModelList.isEmpty() && multinetDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("multinetDataModelList", multinetDataModelList);
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
    
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo){
        Map<String, Object> data = new HashMap<>();
        LocalDate date = CommonService.convertStringToLocalDate(csvRecord.get(4), "M/dd/yyyy h:mm:ss a");
        Map<String, Object> processData = processData(csvRecord);
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", CommonService.convertLocalDateToString(date));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", csvRecord.get(17));
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", processData.get("benificiaryAccount"));
        data.put("beneficiaryMobile", csvRecord.get(12));
        data.put("bankName", processData.get("bankName"));
        data.put("bankCode", processData.get("bankCode"));
        data.put("branchName", processData.get("branchName"));
        data.put("branchCode", processData.get("branchCode"));
        data.put("draweeBranchName", "");
        data.put("draweeBranchCode", "");
        data.put("purposeOfRemittance", csvRecord.get(15));
        data.put("sourceOfIncome", csvRecord.get(16));
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }

    public Map<String, Object> processData(CSVRecord csvRecord){
        Map<String, Object> resp = new HashMap<>();
        String benificiaryAccount = csvRecord.get(7).trim();
        String bankName = csvRecord.get(8).trim();
        String bankCode = csvRecord.get(11).trim();
        String branchName = csvRecord.get(10).trim();
        String branchCode = CommonService.fixRoutingNo(csvRecord.get(9).trim());
        String benificiaryMobile = csvRecord.get(12).trim();
        if(benificiaryAccount.isEmpty()){ //for coc
            benificiaryAccount = "COC" + benificiaryMobile;
            bankName = csvRecord.get(13).trim();
            bankCode = csvRecord.get(14).trim();
        }
        resp.put("benificiaryAccount", benificiaryAccount);
        resp.put("bankName", bankName);
        resp.put("bankCode", bankCode);
        resp.put("branchName", branchName);
        resp.put("branchCode", branchCode);
        return resp;
    }


}
