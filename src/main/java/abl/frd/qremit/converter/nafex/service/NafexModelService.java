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

@Service
public class NafexModelService {
    @Autowired
    NafexModelRepository nafexModelRepository;
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
    ErrorDataModelRepository errorDataModelRepository;
    @Autowired
    FileInfoModelService fileInfoModelService;
    LocalDateTime currentDateTime = LocalDateTime.now();
    //public FileInfoModel save(MultipartFile file, int userId, String exchangeCode, String nrtaCode) {
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode) {
        Map<String, Object> resp = new HashMap<>();
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            
            Map<String, Object> nafexData = csvToNafexModels(file.getInputStream(), user, fileInfoModel, file.getOriginalFilename(), exchangeCode, nrtaCode);
            List<NafexEhMstModel> nafexModels = (List<NafexEhMstModel>) nafexData.get("nafexDataModelList");

            if(nafexData.containsKey("errorMessage")){
                resp.put("errorMessage", nafexData.get("errorMessage"));
            }

            //List<NafexEhMstModel> nafexModels = csvToNafexModels(file.getInputStream(), user, fileInfoModel, file.getOriginalFilename(), exchangeCode, nrtaCode);
            if(nafexModels.size()!=0) {
                for (NafexEhMstModel nafexModel : nafexModels) {
                    nafexModel.setFileInfoModel(fileInfoModel);
                    nafexModel.setUserModel(user);
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                /*
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(nafexModels,"getCheckT24", currentDateTime);
                List<CocModel> cocModelList = CommonService.generateCocModelList(nafexModels,"getCheckCoc", currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(nafexModels,"getCheckAccPayee", currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(nafexModels,"getCheckBeftn", currentDateTime);
                */
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(nafexModels, currentDateTime, 0);
                List<CocModel> cocModelList = CommonService.generateCocModelList(nafexModels, currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(nafexModels, currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(nafexModels, currentDateTime);

                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(nafexModels.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setNafexEhMstModel(nafexModels);
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
                resp.put("fileInfoModel", fileInfoModel);
                //return fileInfoModel;
            }
            else {
                //return null;
                return resp;
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
        return resp;
    }
    
    //public List<NafexEhMstModel> csvToNafexModels(InputStream is, User user, FileInfoModel fileInfoModel, String fileName, String exchangeCode, String nrtaCode) {
    public Map<String, Object> csvToNafexModels(InputStream is, User user, FileInfoModel fileInfoModel, String fileName, String exchangeCode, String nrtaCode) {
        Map<String, Object> resp = new HashMap<>();
        Optional<NafexEhMstModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|') .withIgnoreHeaderCase().withTrim())) {
            List<NafexEhMstModel> nafexDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            String exchangeMessage = "";
            String errorMessage = "";
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = nafexModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                String transactionNo = csvRecord.get(1).trim();
                
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    //errorMessage = "Duplicate Reference No " + csvRecord.get(1) + " Found";
                    //CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                    continue;
                }
                //check exchange code
                exchangeMessage = CommonService.checkExchangeCode(csvRecord.get(0), exchangeCode, nrtaCode);
                if(!exchangeMessage.isEmpty())  break;

                //a/c no, benficiary name, amount empty or null check
                errorMessage = CommonService.checkBeneficiaryNameOrAmountOrBeneficiaryAccount(beneficiaryAccount, csvRecord.get(6), csvRecord.get(3));
                if(!errorMessage.isEmpty()){
                    CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                    continue;
                }
                if(CommonService.isBeftnFound(bankName, beneficiaryAccount, branchCode)){
                    errorMessage = CommonService.checkBEFTNRouting(branchCode);
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isCocFound(beneficiaryAccount)){
                    errorMessage = CommonService.checkCOCBankName(bankName);
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isAccountPayeeFound(bankName, beneficiaryAccount, branchCode)){
                    //check ABL A/C starts with 02** and routing no is not matched with ABL
                    errorMessage = CommonService.checkABLAccountAndRoutingNo(beneficiaryAccount, branchCode, bankName);
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    //abl routing number a/c starts 02** which isn't 13 digits
                    errorMessage = CommonService.checkCOString(beneficiaryAccount);
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isOnlineAccoutNumberFound(beneficiaryAccount)){
                    
                }
                if(transactionList.contains(transactionNo)){
                    duplicateMessage += "Duplicate Transaction No " + transactionNo + " Found <br>";
                    continue;
                }else transactionList.add(transactionNo);
                NafexEhMstModel nafexDataModel = new NafexEhMstModel(
                        exchangeCode, //exCode
                        transactionNo, //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        csvRecord.get(17), //remitterMobile
                        csvRecord.get(6), // beneficiary
                        beneficiaryAccount, //beneficiaryAccount
                        csvRecord.get(12), //beneficiaryMobile
                        bankName, //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        branchCode, // branchCode
                        csvRecord.get(13), //draweeBranchName
                        csvRecord.get(14), //draweeBranchCode
                        csvRecord.get(15), //purposeOfRemittance
                        csvRecord.get(16), //sourceOfIncome
                        "",    //processed_flag
                        CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode), //type_flag
                        "", //Processed_by
                        "",     //processed_date
                        currentDateTime);
                        //CommonService.putOnlineFlag(csvRecord.get(7).trim(), csvRecord.get(8).trim()),  //checkT24
                        //CommonService.putCocFlag(csvRecord.get(7).trim()),  //checkCoc
                        //CommonService.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim(), csvRecord.get(11)),   //checkAccPayee
                        //CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim(), csvRecord.get(11)));        // Checking Beftn
                nafexDataModelList.add(nafexDataModel);
            }
            if (!errorDataModelList.isEmpty()) {
                List<ErrorDataModel> errorDataModels = errorDataModelRepository.saveAll(errorDataModelList);
                int errorCount = errorDataModels.size();
                fileInfoModel.setErrorCount(errorCount);
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && nafexDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("nafexDataModelList", nafexDataModelList);
            if(!duplicateMessage.isEmpty())  resp.put("errorMessage", duplicateMessage);
            if(!exchangeMessage.isEmpty())  resp.put("errorMessage", exchangeMessage);
            return resp;
            //return nafexDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
