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
import abl.frd.qremit.converter.model.SunmanModel;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.SunmanModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class SunmanModelService {
    @Autowired
    SunmanModelRepository sunmanModelRepository;
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

            Map<String, Object> sunmanData = csvToSunmanModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<SunmanModel> sunmanModels = (List<SunmanModel>) sunmanData.get("sunmanDataModelList");

            if(sunmanData.containsKey("errorMessage")){
                resp.put("errorMessage", sunmanData.get("errorMessage"));
            }
            if(sunmanData.containsKey("errorCount") && ((Integer) sunmanData.get("errorCount") >= 1)){
                int errorCount = (Integer) sunmanData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(sunmanModels.size()!=0) {
                for(SunmanModel sunmanModel : sunmanModels){
                    sunmanModel.setFileInfoModel(fileInfoModel);
                    sunmanModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(sunmanModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(sunmanModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setSunmanModel(sunmanModels);
   
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

    public Map<String, Object> csvToSunmanModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<SunmanModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-16LE"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            int i = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                duplicateData = sunmanModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                Map<String, Object> processData = processData(csvRecord);
                String beneficiaryAccount = processData.get("benificiaryAccount").toString();
                String bankName = processData.get("bankName").toString();
                String branchCode = processData.get("branchCode").toString();
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode, processData);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, SunmanModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<SunmanModel> sunmanDataModelList = (List<SunmanModel>) modelResp.get("modelList");
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
            if(errorDataModelList.isEmpty() && sunmanDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("sunmanDataModelList", sunmanDataModelList);
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

    public Map<String, Object> processData(CSVRecord csvRecord){
        Map<String, Object> resp = new HashMap<>();
        String benificiaryAccount = csvRecord.get(7).trim();
        String bankName = csvRecord.get(8).trim();
        String branchName = csvRecord.get(10).trim();
        String branchCode = CommonService.fixRoutingNo(csvRecord.get(12).trim());
        String benificiaryMobile = csvRecord.get(13).trim();
        String draweeBranchName = csvRecord.get(14).trim();
        String draweeBranchCode = csvRecord.get(15).trim();
        String purposeOfRemittance = csvRecord.get(16).trim();
        String sourceOfIncome = csvRecord.get(17).trim();
        String remitterMobile; 
        
        if(benificiaryAccount.isEmpty()){ //for coc
            benificiaryAccount = csvRecord.get(12).trim();
            bankName = csvRecord.get(13).trim();
            branchName = csvRecord.get(14).trim();
            draweeBranchCode = "";
            draweeBranchName = "";
            branchCode = "";
            benificiaryMobile = csvRecord.get(12).trim().replace("COC", "");
            purposeOfRemittance = csvRecord.get(15).trim();
            sourceOfIncome = csvRecord.get(16).trim();
            remitterMobile = csvRecord.get(17).trim();
        }else remitterMobile = csvRecord.get(18).trim();  
        resp.put("benificiaryAccount", benificiaryAccount);
        resp.put("bankName", bankName);
        resp.put("branchName", branchName);
        resp.put("branchCode", branchCode);
        resp.put("draweeBranchName", draweeBranchName);
        resp.put("draweeBranchCode", draweeBranchCode);
        resp.put("purposeOfRemittance", purposeOfRemittance);
        resp.put("sourceOfIncome", sourceOfIncome);
        resp.put("remitterMobile", remitterMobile);
        resp.put("benificiaryMobile", benificiaryMobile);
        return resp;
    }
    
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, 
        String branchCode, Map<String, Object> processData){
        Map<String, Object> data = new HashMap<>();
        LocalDate date = CommonService.convertStringToLocalDate(csvRecord.get(4), "dd-MM-yyyy");
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", CommonService.convertLocalDateToString(date));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", processData.get("remitterMobile").toString());
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(12));
        data.put("bankName", bankName);
        data.put("bankCode", csvRecord.get(9));
        data.put("branchName", processData.get("branchName").toString());
        data.put("branchCode", branchCode);
        data.put("draweeBranchName", processData.get("draweeBranchName").toString());
        data.put("draweeBranchCode", processData.get("draweeBranchCode").toString());
        data.put("purposeOfRemittance", processData.get("purposeOfRemittance").toString());
        data.put("sourceOfIncome", processData.get("sourceOfIncome").toString());
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }


}
