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
public class AgexSingaporeModelService {
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
    AgexSingaporeModelRepository agexSingaporeModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId, String exchangeCode, String fileType) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);

            int type = 0;
            if(fileType.equalsIgnoreCase("API")) type = 1;
            List<AgexSingaporeModel> agexSingaporeModelList = csvToAgexSingaporeModels(file.getInputStream(),type);

            if(agexSingaporeModelList.size()!=0) {
                int ind = 0;
                for (AgexSingaporeModel agexSingaporeModel : agexSingaporeModelList) {
                    agexSingaporeModel.setExchangeCode(exchangeCode);
                    agexSingaporeModel.setFileInfoModel(fileInfoModel);
                    agexSingaporeModel.setUserModel(user);
                    if (ind == 0) {
                        fileInfoModel.setExchangeCode(agexSingaporeModel.getExchangeCode());
                        ind++;
                    }
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                /*
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(agexSingaporeModelList, "getCheckT24",type, currentDateTime);
                List<CocModel> cocModelList = CommonService.generateCocModelList(agexSingaporeModelList, "getCheckCoc", currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(agexSingaporeModelList, "getCheckAccPayee", currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(agexSingaporeModelList, "getCheckBeftn", currentDateTime);
                */
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(agexSingaporeModelList, currentDateTime, type);
                List<CocModel> cocModelList = CommonService.generateCocModelList(agexSingaporeModelList, currentDateTime);
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generateAccountPayeeModelList(agexSingaporeModelList, currentDateTime);
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(agexSingaporeModelList, currentDateTime);


                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
                fileInfoModel.setTotalCount(String.valueOf(agexSingaporeModelList.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setIsSettlement(type);
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setAgexSingaporeModel(agexSingaporeModelList);
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
    public List<AgexSingaporeModel> csvToAgexSingaporeModels(InputStream is, int type) {
        Optional<AgexSingaporeModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<AgexSingaporeModel> agexSingaporeModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = agexSingaporeModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                String bankName = (type == 1) ? csvRecord.get(8): csvRecord.get(9);
                String bankCode = (type == 1) ? csvRecord.get(9): csvRecord.get(8);
                AgexSingaporeModel agexSingaporeModel = new AgexSingaporeModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        "", // remitterMobile
                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "", // beneficiaryMobile
                        bankName, //bankName
                        bankCode, //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "", //Drawee Branch Name
                        "", // Drawee Branch Code
                        "", // purposeOfRemittance
                        "", // sourceOfIncome
                        "",    // processed_flag
                        CommonService.setTypeFlag(csvRecord.get(7).trim(), bankName, csvRecord.get(11).trim()), //type_flag
                        "",      // Processed_by
                        "",    // processed_date
                        currentDateTime);
                agexSingaporeModelList.add(agexSingaporeModel);
            }
            return agexSingaporeModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
