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
public class RiaModelService {
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
    RiaModelRepository riaModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<RiaModel> riaModelList = csvToRiaModels(file.getInputStream());
            ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findExchangeCodeByBaseTableName("ria");
            if(riaModelList.size()!=0) {
                int ind = 0;
                for (RiaModel riaModel : riaModelList) {
                    riaModel.setExchangeCode(exchangeHouseModel.getExchangeCode());
                    riaModel.setFileInfoModel(fileInfoModel);
                    riaModel.setUserModel(user);
                    if (ind == 0) {
                        fileInfoModel.setExchangeCode(riaModel.getExchangeCode());
                        ind++;
                    }
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(riaModelList, "getCheckT24");
                List<CocModel> cocModelList = CommonService.generateCocModelList(riaModelList, "getCheckCoc");
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(riaModelList, "getCheckAccPayee");
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(riaModelList, "getCheckBeftn");


                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(riaModelList.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setProcessedCount("test");
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setRiaModel(riaModelList);
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
    public List<RiaModel> csvToRiaModels(InputStream is) {
        Optional<RiaModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<RiaModel> riaModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = riaModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                RiaModel riaModel = new RiaModel(
                        "7010290", //exCode
                        csvRecord.get(0), //Tranno
                        "BDT", //Currency
                        Double.parseDouble(csvRecord.get(1)), //Amount
                        csvRecord.get(10), //enteredDate
                        csvRecord.get(3), //remitter

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "Beneficiary Mobile", // beneficiaryMobile
                        "Bank Name", //bankName
                        "Bank Code", //bankCode
                        "Branch Name", //branchName
                        "Branch Code", // branchCode
                        "Drawee Branch Name", //Drawee Branch Name
                        "Drawee Branch Code", // Drawee Branch Code
                        "Purpose Of Remittance", // purposeOfRemittance
                        "Source Of Income", // sourceOfIncome
                        "Remitter Mobile", // remitterMobile
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        "extraC",
                        CommonService.putOnlineFlag(csvRecord.get(7).trim()),                                 // checkT24
                        CommonService.putCocFlag(csvRecord.get(7).trim()),                                    //checkCoc
                        "0",
                        "0");
                        //CommonService.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim()),   //checkAccPayee
                        //CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim()));        // Checking Beftn
                riaModelList.add(riaModel);
            }
            return riaModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}