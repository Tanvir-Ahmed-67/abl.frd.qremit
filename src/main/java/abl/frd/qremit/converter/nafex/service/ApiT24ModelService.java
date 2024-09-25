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
import java.util.Map;
import java.util.Optional;

@Service
public class ApiT24ModelService {
    @Autowired
    ApiT24ModelRepository apiT24ModelRepository;
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
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
            Map<String, String> nrtaCodeVsExchangeCodeMap = CommonService.getNrtaCodeVsExchangeCodeMap(exchangeHouseModelList);
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<ApiT24Model> apiT24Models = csvToApiT24Models(file.getInputStream());
            if(apiT24Models.size()!=0) {
                int ind=0;
                for(ApiT24Model apiT24Model : apiT24Models){
                    apiT24Model.setExchangeCode(nrtaCodeVsExchangeCodeMap.get(apiT24Model.getExchangeCode()));
                    apiT24Model.setFileInfoModel(fileInfoModel);
                    apiT24Model.setUserModel(user);
                    if(ind==0) {
                        fileInfoModel.setExchangeCode(exchangeCode);
                        ind++;
                    }

                }

                // Online DATA TABLE GENERATION GOING ON HERE
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(apiT24Models,"getCheckT24", currentDateTime);

                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount("0");
                fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
                fileInfoModel.setBeftnCount("0");
                fileInfoModel.setCocCount("0");
                fileInfoModel.setTotalCount(String.valueOf(apiT24Models.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setIsSettlement(1);
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setApiT24Model(apiT24Models);
                fileInfoModel.setOnlineModelList(onlineModelList);

                for (OnlineModel onlineModel:onlineModelList){
                    onlineModel.setIsProcessed(1);
                    onlineModel.setIsDownloaded(1);
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
    public List<ApiT24Model> csvToApiT24Models(InputStream is) {
        Optional<ApiT24Model> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<ApiT24Model> apiT24ModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = apiT24ModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                ApiT24Model apiT24Model = new ApiT24Model(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        "Remiter Mobile", //remitterMobile

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "Beneficiary Mobile", //beneficiaryMobile
                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "Drawee Branch Name", //draweeBranchName
                        "Drawee Branch Code", //draweeBranchCode
                        "Purpose of Remitance", //purposeOfRemittance
                        "Source Of Income", //sourceOfIncome
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        currentDateTime,
                        CommonService.putOnlineFlag(csvRecord.get(7).trim(), csvRecord.get(8).trim()),                              // checkT24
                        CommonService.putCocFlag(csvRecord.get(7).trim()),                                                         //checkCoc
                        CommonService.putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim(), csvRecord.get(11)),   //checkAccPayee
                        CommonService.putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim(), csvRecord.get(11)));        //checkBeftn
                apiT24ModelList.add(apiT24Model);
            }
            return apiT24ModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
