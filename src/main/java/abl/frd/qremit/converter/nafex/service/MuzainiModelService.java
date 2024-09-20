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
public class MuzainiModelService {
    @Autowired
    MuzainiModelRepository muzainiModelRepository;
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
            List<MuzainiModel> muzainiModels = csvToMuzainiModels(file.getInputStream());
            if(muzainiModels.size()!=0) {
                int ind = 0;
                for (MuzainiModel muzainiModel : muzainiModels) {
                    muzainiModel.setExchangeCode(exchangeCode);
                    muzainiModel.setFileInfoModel(fileInfoModel);
                    muzainiModel.setUserModel(user);
                    if (ind == 0) {
                        fileInfoModel.setExchangeCode(muzainiModel.getExchangeCode());
                        ind++;
                    }

                }

                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(muzainiModels,"getCheckT24", currentDateTime);
                List<CocModel> cocModelList = CommonService.generateCocModelList(muzainiModels,"getCheckCoc", currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(muzainiModels,"getCheckAccPayee", currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(muzainiModels,"getCheckBeftn", currentDateTime);


                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(muzainiModels.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setMuzainiModel(muzainiModels);
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
    public List<MuzainiModel> csvToMuzainiModels(InputStream is) {
        Optional<MuzainiModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|') .withIgnoreHeaderCase().withTrim())) {
            List<MuzainiModel> muzainiDataModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = muzainiModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                MuzainiModel muzainiDataModel = new MuzainiModel(

                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        csvRecord.get(14), //remitterMobile

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        csvRecord.get(12), //beneficiaryMobile

                        csvRecord.get(8), //bankCode
                        csvRecord.get(9), //bankName
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "draweeBranchName", //draweeBranchName
                        "draweeBranchCode", //draweeBranchCode
                        csvRecord.get(13), //purposeOfRemittance
                        "sourceOfIncome", //sourceOfIncome
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        currentDateTime,           // extra_c
                        CommonService.putOnlineFlag(csvRecord.get(7).trim(), csvRecord.get(8).trim()),                          // checkT24
                        CommonService.putCocFlag(csvRecord.get(7).trim()),                                                       //checkCoc
                        CommonService.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim(), csvRecord.get(11)),   //checkAccPayee
                        CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim(),csvRecord.get(11)));        //checkBeftn

                muzainiDataModelList.add(muzainiDataModel);
            }
            return muzainiDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
