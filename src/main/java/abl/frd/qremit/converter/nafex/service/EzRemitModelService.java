package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class EzRemitModelService {
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
    EzRemitModelRepository ezRemitModelRepository;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String fileType, String nrtaCode) {
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
            /*
            if(fileType.equalsIgnoreCase("API")){
               ezRemitModelList = csvToEzRemitAccountPayeeModels(file.getInputStream(), exchangeCode);
            }else if(fileType.equalsIgnoreCase("BEFTN")){
                ezRemitModelList = csvToEzRemitBEFTNModels(file.getInputStream(), exchangeCode);
            }
            */
            Map<String, Object> ezRemitData = csvToEzRemitModels(file.getInputStream(), type, user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);
            List<EzRemitModel> ezRemitModelList = (List<EzRemitModel>) ezRemitData.get("ezRemitModelList");
            if(ezRemitData.containsKey("errorMessage")){
                resp.put("errorMessage", ezRemitData.get("errorMessage"));
            }
            if(ezRemitData.containsKey("errorCount") && ((Integer) ezRemitData.get("errorCount") >= 1)){
                int errorCount = (Integer) ezRemitData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            
            if(ezRemitModelList.size()!=0) {
                for (EzRemitModel ezRemitModel : ezRemitModelList) {
                    ezRemitModel.setFileInfoModel(fileInfoModel);
                    ezRemitModel.setUserModel(user);
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(ezRemitModelList, fileInfoModel, user, currentDateTime, type);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(ezRemitModelList.size()));
                fileInfoModel.setIsSettlement(type);
                fileInfoModel.setEzRemitModel(ezRemitModelList);
                // SAVING TO MySql Data Table
                try{
                    fileInfoModelRepository.save(fileInfoModel);                
                    resp.put("fileInfoModel", fileInfoModel);
                }catch(Exception e){
                    resp.put("errorMessage", e.getMessage());
                }

                /*
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(ezRemitModelList, "getCheckT24", type, currentDateTime);
                List<CocModel> cocModelList = CommonService.generateCocModelList(ezRemitModelList, "getCheckCoc", currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(ezRemitModelList, "getCheckAccPayee", currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(ezRemitModelList, "getCheckBeftn", currentDateTime);
                */
                /*
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(ezRemitModelList, currentDateTime, type);
                List<CocModel> cocModelList = CommonService.generateCocModelList(ezRemitModelList, currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(ezRemitModelList, currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(ezRemitModelList, currentDateTime);


                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(ezRemitModelList.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setIsSettlement(type);
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setEzRemitModel(ezRemitModelList);
                fileInfoModel.setCocModelList(cocModelList);
                fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
                fileInfoModel.setBeftnModelList(beftnModelList);
                fileInfoModel.setOnlineModelList(onlineModelList);

                for (CocModel cocModel : cocModelList) {
                    cocModel.setFileInfoModel(fileInfoModel);
                    cocModel.setUserModel(user);
                }
                for (AccountPayeeModel accountPayeeModel : accountPayeeModelList) {
                    accountPayeeModel.setFileInfoModel(fileInfoModel);
                    accountPayeeModel.setUserModel(user);
                }
                for (BeftnModel beftnModel : beftnModelList) {
                    beftnModel.setFileInfoModel(fileInfoModel);
                    beftnModel.setUserModel(user);
                }
                for (OnlineModel onlineModel : onlineModelList) {
                    onlineModel.setFileInfoModel(fileInfoModel);
                    onlineModel.setUserModel(user);
                }
                // SAVING TO MySql Data Table
                fileInfoModelRepository.save(fileInfoModel);
                return fileInfoModel;
                */
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> csvToEzRemitModels(InputStream is, int type, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime){
        Map<String, Object> resp = new HashMap<>();
        Optional<EzRemitModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<EzRemitModel> ezRemitModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                int length = csvRecord.size();
                Map<String, Object> apiCheckResp = checkEzRemitApiOrBeftnData(length, type);
                if((Integer) apiCheckResp.get("err") == 1){
                    resp.put("errorMessage", apiCheckResp.get("msg"));
                    break;
                }
                String bankName = (type == 1) ? "Agrani Bank": csvRecord.get(9).trim();
                String branchCode = (type == 1) ? "": csvRecord.get(11).trim();
                branchCode = CommonService.fixRoutingNo(branchCode);
                String transactionNo = (type == 1) ? csvRecord.get(0).trim(): csvRecord.get(1).trim();
                String beneficiaryAccount = (type == 1) ? csvRecord.get(4).trim(): csvRecord.get(7).trim();
                String amount = (type == 1) ? csvRecord.get(5) : csvRecord.get(3);
                Map<String, Object> data = getCsvData(csvRecord, type, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode, amount);
                duplicateData = ezRemitModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, csvRecord.get(0).trim(), duplicateData, transactionList);
                if((Integer) errResp.get("err") == 1){
                    errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                    continue;
                }
                if((Integer) errResp.get("err") == 2){
                    resp.put("errorMessage", errResp.get("msg"));
                    break;
                }
                if((Integer) errResp.get("err") == 3){
                    duplicateMessage += errResp.get("msg");
                    duplicateCount++;
                    continue;
                }
                if((Integer) errResp.get("err") == 4){
                    duplicateMessage += errResp.get("msg");
                    continue;
                }
                if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");
                EzRemitModel ezRemitModel = new EzRemitModel();
                ezRemitModel = CommonService.createDataModel(ezRemitModel, data);
                ezRemitModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                ezRemitModel.setUploadDateTime(currentDateTime);
                ezRemitModelList.add(ezRemitModel);
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && ezRemitModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("ezRemitModelList", ezRemitModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        }catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> getCsvData(CSVRecord csvRecord, int type, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode, String amount){
        String bankCode = (type == 1) ? "11": csvRecord.get(8).trim();
        String branchName = (type == 1) ? "": csvRecord.get(10).trim();
        String currrency = (type == 1) ? "BDT": csvRecord.get(2);
        //String amount = (type == 1) ? csvRecord.get(5) : csvRecord.get(3);
        String enteredDate = (type == 1) ? csvRecord.get(7) : csvRecord.get(4);
        String remiterName = (type == 1) ? csvRecord.get(1) : csvRecord.get(5);

        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", currrency);
        data.put("amount", amount);
        data.put("enteredDate", enteredDate);
        data.put("remitterName", remiterName);
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", "");
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("draweeBranchName", "");
        data.put("draweeBranchCode", "");
        data.put("purposeOfRemittance", "");
        data.put("sourceOfIncome", "");
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }

    public Map<String, Object> checkEzRemitApiOrBeftnData(int length, int type){
        Map<String, Object> resp = CommonService.getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        if(type == 1 && length != 8)    resp = CommonService.getResp(1, msg, null);
        else if(type == 0 && length != 12)  resp = CommonService.getResp(1, msg, null);
        return resp;
    }
/* 
    public List<EzRemitModel> csvToEzRemitAccountPayeeModels(InputStream is, String exchangeCode) {
        Optional<EzRemitModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<EzRemitModel> ezRemitModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = ezRemitModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(0));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                String bankName = "Agrani Bank";
                EzRemitModel ezRemitModel = new EzRemitModel(
                        exchangeCode, //exCode
                        csvRecord.get(0), //Tranno
                        "BDT", //Currency
                        Double.parseDouble(csvRecord.get(5)), //Amount
                        csvRecord.get(7), //enteredDate
                        csvRecord.get(1), //remitter
                        "", // remitterMobile
                        csvRecord.get(6), // beneficiary
                        csvRecord.get(4), //beneficiaryAccount
                        "", // beneficiaryMobile
                        bankName, //bankName
                        "11", //bankCode
                        "", //branchName
                        "", // branchCode
                        "", //Drawee Branch Name
                        "", // Drawee Branch Code
                        "", // purposeOfRemittance
                        "", // sourceOfIncome
                        "",    // processed_flag
                        CommonService.setTypeFlag(csvRecord.get(4).trim(), bankName, ""), //type_flag
                        "",      // Processed_by
                        "",            // processed_date
                        currentDateTime);
                ezRemitModelList.add(ezRemitModel);
            }
            return ezRemitModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
    public List<EzRemitModel> csvToEzRemitBEFTNModels(InputStream is, String exchangeCode) {
        Optional<EzRemitModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<EzRemitModel> ezRemitModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = ezRemitModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                EzRemitModel ezRemitModel = new EzRemitModel(
                        exchangeCode, //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        "", // remitterMobile

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "", // beneficiaryMobile

                        csvRecord.get(9),  //bankName
                        csvRecord.get(8),  //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "", //Drawee Branch Name
                        "", // Drawee Branch Code
                        "", // purposeOfRemittance
                        "", // sourceOfIncome
                        "",    // processed_flag
                        CommonService.setTypeFlag(csvRecord.get(7).trim(), csvRecord.get(9).trim(), csvRecord.get(11).trim()), //type_flag
                        "",      // Processed_by
                        "",            // processed_date
                        currentDateTime);
                ezRemitModelList.add(ezRemitModel);
            }
            return ezRemitModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
        */

}