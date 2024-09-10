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
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId, String exchangeCode) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            //FileInfoModel fModel = fileInfoModelRepository.findByFileName(file.getOriginalFilename());
            
            List<NafexEhMstModel> nafexModels = csvToNafexModels(file.getInputStream(), user, fileInfoModel, file.getOriginalFilename(), exchangeCode);
            if(nafexModels.size()!=0) {
                int ind = 0;
            
                for (NafexEhMstModel nafexModel : nafexModels) {
                    //nafexModel.setExchangeCode(exchangeCode);
                    nafexModel.setFileInfoModel(fileInfoModel);
                    nafexModel.setUserModel(user);
                    if (ind == 0) {
                        fileInfoModel.setExchangeCode(exchangeCode);
                        ind++;
                    }
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(nafexModels,"getCheckT24");
                List<CocModel> cocModelList = CommonService.generateCocModelList(nafexModels,"getCheckCoc");
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(nafexModels,"getCheckAccPayee");
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(nafexModels,"getCheckBeftn");


                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(nafexModels.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setProcessedCount("test");
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
                return fileInfoModel;
            }
            else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
    public List<NafexEhMstModel> csvToNafexModels(InputStream is, User user, FileInfoModel fileInfoModel, String fileName, String exchangeCode) {
        Optional<NafexEhMstModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|') .withIgnoreHeaderCase().withTrim())) {
            List<NafexEhMstModel> nafexDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            String errorMessage = "";
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = nafexModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    //errorMessage = "Duplicate Reference No " + csvRecord.get(1) + " Found";
                    //CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                    continue;
                }
                //a/c no, benficiary name, amount empty or null check
                /* 
                if(CommonService.checkEmptyString(csvRecord.get(7)) ||  CommonService.checkEmptyString(csvRecord.get(6)) || CommonService.checkEmptyString(csvRecord.get(3))){
                    errorMessage = "A/C Number or Beneficiary Name or Amount Can not be empty";
                    CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                    continue;
                }
                */
                errorMessage = CommonService.checkBeneficiaryNameOrAmountOrBeneficiaryAccount(csvRecord.get(7), csvRecord.get(6), csvRecord.get(3));
                if(!errorMessage.isEmpty()){
                    CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                    continue;
                }
                
                if(CommonService.isBeftnFound(csvRecord.get(8), csvRecord.get(7), csvRecord.get(11))){
                    /*
                    if(csvRecord.get(11).length() != 9 || CommonService.checkAgraniRoutingNo(csvRecord.get(11))){
                        errorMessage = "Invalid Routing Number for BEFTN";
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;  
                    }
                    */
                    errorMessage = CommonService.checkBEFTNRouting(csvRecord.get(11));
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isCocFound(csvRecord.get(7))){
                    /* 
                    if(!CommonService.checkAgraniBankName(csvRecord.get(8))){
                        errorMessage = "Invalid Bank Name";
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    */
                    errorMessage = CommonService.checkCOCBankName(csvRecord.get(8));
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isAccountPayeeFound(csvRecord.get(8), csvRecord.get(7), csvRecord.get(11))){
                    //check ABL A/C starts with 02** and routing no is not matched with ABL
                    /*
                    if(CommonService.isOnlineAccoutNumberFound(csvRecord.get(7)) && !CommonService.checkAgraniRoutingNo(csvRecord.get(11)) 
                        && !CommonService.checkAgraniBankName(csvRecord.get(8))){
                        errorMessage = "Invalid Routing Number or Bank Name";
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    */
                    errorMessage = CommonService.checkABLAccountAndRoutingNo(csvRecord.get(7), csvRecord.get(11), csvRecord.get(8));
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    //abl routing number a/c starts 02** which isn't 13 digits
                    /* 
                    else if(CommonService.checkAgraniRoutingNo(csvRecord.get(11)) && csvRecord.get(7).startsWith("02000")){
                        if(csvRecord.get(7).length() != 13){
                            errorMessage = "Invalid ABL Online A/C Number which requires 13 digits";
                            CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                            continue;
                        }
                    }
                    */
                    //string starts with CO
                    /* 
                    else if(csvRecord.get(7).toLowerCase().contains("co")){
                        errorMessage = "Invalid COC A/C name";
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    */
                    errorMessage = CommonService.checkCOString(csvRecord.get(7));
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, csvRecord, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }else if(CommonService.isOnlineAccoutNumberFound(csvRecord.get(7))){
                    
                }
                
                NafexEhMstModel nafexDataModel = new NafexEhMstModel(
                        exchangeCode, //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        csvRecord.get(17), //remitterMobile

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        csvRecord.get(12), //beneficiaryMobile
                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode

                        csvRecord.get(13), //draweeBranchName
                        csvRecord.get(14), //draweeBranchCode
                        csvRecord.get(15), //purposeOfRemittance
                        csvRecord.get(16), //sourceOfIncome
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        "extraC",
                        CommonService.putOnlineFlag(csvRecord.get(7).trim(), csvRecord.get(8).trim()),                                 // checkT24
                        CommonService.putCocFlag(csvRecord.get(7).trim()),                                    //checkCoc
                        CommonService.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim(), csvRecord.get(11)),   //checkAccPayee
                        CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim(), csvRecord.get(11)));        // Checking Beftn
                nafexDataModelList.add(nafexDataModel);
            }
            if (!errorDataModelList.isEmpty()) {
                errorDataModelRepository.saveAll(errorDataModelList);
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && nafexDataModelList.isEmpty()){
                //fileInfoModel.getId()
            }
            return nafexDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
