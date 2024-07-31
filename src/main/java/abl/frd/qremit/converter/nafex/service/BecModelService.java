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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BecModelService {
    @Autowired
    BecModelRepository becModelRepository;
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
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId, String exchangeCode) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<BecModel> becModels = csvToBecModels(file.getInputStream());
            if(becModels.size()!=0) {
            int ind=0;
            for(BecModel becModel : becModels){
                becModel.setExchangeCode(exchangeCode); 
                becModel.setFileInfoModel(fileInfoModel);
                becModel.setUserModel(user);
                if(ind==0) {
                    fileInfoModel.setExchangeCode(exchangeCode);
                    ind++;
                }

            }

            // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
            List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(becModels,"getCheckT24");
            List<CocModel> cocModelList = CommonService.generateCocModelList(becModels,"getCheckCoc");
            List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(becModels,"getCheckAccPayee");
            List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(becModels,"getCheckBeftn");


            // FILE INFO TABLE GENERATION HERE......
            fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
            fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
            fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
            fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
            fileInfoModel.setTotalCount(String.valueOf(becModels.size()));
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setProcessedCount("test");
            fileInfoModel.setUnprocessedCount("test");
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModel.setBecModel(becModels);
            fileInfoModel.setCocModelList(cocModelList);
            fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
            fileInfoModel.setBeftnModelList(beftnModelList);
            fileInfoModel.setOnlineModelList(onlineModelList);

            for(CocModel cocModel:cocModelList){
                cocModel.setFileInfoModel(fileInfoModel);
                cocModel.setUserModel(user);
            }
            for (AccountPayeeModel accountPayeeModel:accountPayeeModelList){
                accountPayeeModel.setFileInfoModel(fileInfoModel);
                accountPayeeModel.setUserModel(user);
            }
            for(BeftnModel beftnModel:beftnModelList){
                beftnModel.setFileInfoModel(fileInfoModel);
                beftnModel.setUserModel(user);
            }
            for (OnlineModel onlineModel:onlineModelList){
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
    public List<BecModel> csvToBecModels(InputStream is) {
        Optional<BecModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<BecModel> becDataModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = becModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                BecModel becDataModel = new BecModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        csvRecord.get(17), //remitterMobile

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount

                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        csvRecord.get(12), //beneficiaryMobile
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
                        CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim(), csvRecord.get(11)));        //checkBeftn
                becDataModelList.add(becDataModel);
            }
            return becDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
