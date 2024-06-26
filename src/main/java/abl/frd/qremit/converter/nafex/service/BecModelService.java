package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.BecModelServiceHelper;
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
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<BecModel> becModels = csvToBecModels(file.getInputStream());
            if(becModels.size()!=0) {
            int ind=0;
            for(BecModel becModel : becModels){
                becModel.setExchangeCode("7010235");
                becModel.setFileInfoModel(fileInfoModel);
                becModel.setUserModel(user);
                if(ind==0) {
                    fileInfoModel.setExchangeCode(becModel.getExchangeCode());
                    ind++;
                }

            }

            // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
            List<OnlineModel> onlineModelList = BecModelServiceHelper.generateOnlineModelList(becModels);
            List<CocModel> cocModelList = BecModelServiceHelper.generateCocModelList(becModels);
            List<AccountPayeeModel> accountPayeeModelList = BecModelServiceHelper.generateAccountPayeeModelList(becModels);
            List<BeftnModel> beftnModelList = BecModelServiceHelper.generateBeftnModelList(becModels);


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
                        csvRecord.get(17), //remitterMobile
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        "extraC",
                        BecModelServiceHelper.putOnlineFlag(csvRecord.get(7).trim()),                                 // checkT24
                        BecModelServiceHelper.putCocFlag(csvRecord.get(7).trim()),                                    //checkCoc
                        BecModelServiceHelper.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim()),   //checkAccPayee
                        BecModelServiceHelper.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim()));        //checkBeftn
                becDataModelList.add(becDataModel);
            }
            return becDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
