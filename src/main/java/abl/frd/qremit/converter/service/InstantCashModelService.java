package abl.frd.qremit.converter.service;

import java.io.*;
import java.time.LocalDateTime;

import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.InstantCashModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.InstantCashModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class InstantCashModelService {
    @Autowired
    InstantCashModelRepository instantCashModelRepository;
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
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String fileType, String nrtaCode, String tbl) {
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
            
            int type = 0;
            if(fileType.equalsIgnoreCase("API")) type = 1;
            Map<String, Object> instantCashData = csvToInstantCashModels(file.getInputStream(), type, user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<InstantCashModel> instantCashModels = (List<InstantCashModel>) instantCashData.get("instantCashDataModelList");

            if(instantCashData.containsKey("errorMessage")){
                resp.put("errorMessage", instantCashData.get("errorMessage"));
            }
            if(instantCashData.containsKey("errorCount") && ((Integer) instantCashData.get("errorCount") >= 1)){
                int errorCount = (Integer) instantCashData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(instantCashModels.size()!=0) {
                for(InstantCashModel instantCashModel : instantCashModels){
                    instantCashModel.setFileInfoModel(fileInfoModel);
                    instantCashModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(instantCashModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(instantCashModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setInstantCashModel(instantCashModels);
   
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

    public Map<String, Object> csvToInstantCashModels(InputStream is, int type, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, 
        LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<InstantCashModel> duplicateData = Optional.empty();
        CSVFormat csvFormat = (type == 1) ? CSVFormat.DEFAULT.withDelimiter('|'): CSVFormat.DEFAULT;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            //CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            CSVParser csvParser = new CSVParser(fileReader, csvFormat.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<InstantCashModel> instantCashDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            String fileExchangeCode = "";
            int isValidFile = 1;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String bankCode = (type == 1) ? csvRecord.get(9).trim(): csvRecord.get(8).trim();
                if(i == 1){
                    
                    Map<String, Object> apiCheckResp = CommonService.checkApiOrBeftnData(bankCode, type);
                    if((Integer) apiCheckResp.get("err") == 1){
                        resp.put("errorMessage", apiCheckResp.get("msg"));
                        isValidFile = 0;
                        break;
                    }
                
                    
                }
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                Map<String, Object> data = getCsvData(type, csvRecord, exchangeCode, bankCode, transactionNo, amount);
                fileExchangeCode = csvRecord.get(0).trim();
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
                

                /*
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                duplicateData = instantCashModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                */
            if(isValidFile == 1){
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                for(Map<String, Object> data: dataList){
                    String transactionNo = data.get("transactionNo").toString();
                    String bankName = data.get("bankName").toString();
                    String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                    String branchCode = data.get("branchCode").toString();
                    Map<String, Object> dupResp = CommonService.getDuplicateTransactionNo(transactionNo, uniqueDataList);
                    if((Integer) dupResp.get("isDuplicate") == 1){
                        duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                        duplicateCount++;
                        continue;
                    }
                
                    Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, fileExchangeCode, duplicateData, transactionList);
                    if((Integer) errResp.get("err") == 1){
                        errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                        continue;
                    }
                    if((Integer) errResp.get("err") == 2){
                        resp.put("errorMessage", errResp.get("msg"));
                        break;
                    }
                    if((Integer) errResp.get("err") == 4){
                        duplicateMessage += errResp.get("msg");
                        continue;
                    }
                    if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");

                    InstantCashModel instantCashDataModel = new InstantCashModel();
                    instantCashDataModel = CommonService.createDataModel(instantCashDataModel, data);
                    instantCashDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                    instantCashDataModel.setUploadDateTime(currentDateTime);
                    instantCashDataModelList.add(instantCashDataModel);
                }
            }

            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && instantCashDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("instantCashDataModelList", instantCashDataModelList);
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
    
    public Map<String, Object> getCsvData(int type, CSVRecord csvRecord, String exchangeCode, String bankCode, String transactionNo, String amount){
        String bankName = (type == 1) ? csvRecord.get(8).trim(): csvRecord.get(9).trim();
        String remitterMobile = (type == 1) ? csvRecord.get(17).trim():"";
        String beneficiaryMobile = (type == 1) ? csvRecord.get(12).trim():"";
        String draweeBranchName = (type == 1) ? csvRecord.get(13).trim():"";
        String draweeBranchCode = (type == 1) ? csvRecord.get(14).trim():"";
        String purposeOfRemittance = (type == 1) ? csvRecord.get(15).trim():"";
        String sourceOfIncome = (type == 1) ? csvRecord.get(16).trim():"";
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", amount);
        data.put("enteredDate", csvRecord.get(4));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", remitterMobile);
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", csvRecord.get(7).trim());
        data.put("beneficiaryMobile", beneficiaryMobile);
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", csvRecord.get(10));
        data.put("branchCode", CommonService.fixRoutingNo(csvRecord.get(11).trim()));
        data.put("draweeBranchName", draweeBranchName);
        data.put("draweeBranchCode", draweeBranchCode);
        data.put("purposeOfRemittance", purposeOfRemittance);
        data.put("sourceOfIncome", sourceOfIncome);
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }


}
