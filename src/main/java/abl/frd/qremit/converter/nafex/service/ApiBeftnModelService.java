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
public class ApiBeftnModelService {
    @Autowired
    ApiBeftnModelRepository apiBeftnModelRepository;
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
            List<ApiBeftnModel> apiBeftnModels = csvToApiBeftnModels(file.getInputStream());
            if(apiBeftnModels.size()!=0) {
                int ind=0;
                for(ApiBeftnModel apiBeftnModel : apiBeftnModels){
                    apiBeftnModel.setExchangeCode(nrtaCodeVsExchangeCodeMap.get(apiBeftnModel.getExchangeCode()));
                    apiBeftnModel.setFileInfoModel(fileInfoModel);
                    apiBeftnModel.setUserModel(user);
                    if(ind==0) {
                        fileInfoModel.setExchangeCode(exchangeCode);
                        ind++;
                    }
                }
                // Beftn DATA TABLE GENERATION GOING ON HERE
               //List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(apiBeftnModels,"getCheckBeftn", currentDateTime);
               List<BeftnModel> beftnModelList = CommonService.generateBeftnModelList(apiBeftnModels, currentDateTime);

                // FILE INFO TABLE GENERATION HERE......
                fileInfoModel.setAccountPayeeCount("0");
                fileInfoModel.setOnlineCount("0");
                fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
                fileInfoModel.setCocCount("0");
                fileInfoModel.setTotalCount(String.valueOf(apiBeftnModels.size()));
                fileInfoModel.setFileName(file.getOriginalFilename());
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setUnprocessedCount("test");
                fileInfoModel.setUploadDateTime(currentDateTime);
                fileInfoModel.setApiBeftnModel(apiBeftnModels);

                fileInfoModel.setBeftnModelList(beftnModelList);
                for(BeftnModel beftnModel:beftnModelList){
                    beftnModel.setFileInfoModel(fileInfoModel);
                    beftnModel.setUserModel(user);
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
    public List<ApiBeftnModel> csvToApiBeftnModels(InputStream is) {
        Optional<ApiBeftnModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<ApiBeftnModel> apiBeftnModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = apiBeftnModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
                    continue;
                }
                ApiBeftnModel apiBeftnModel = new ApiBeftnModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter
                        "", //remitterMobile
                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "", //beneficiaryMobile
                        csvRecord.get(9), //bankName
                        csvRecord.get(8), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "", //draweeBranchName
                        "", //draweeBranchCode
                        "", //purposeOfRemittance
                        "", //sourceOfIncome
                        "",    // processed_flag
                        CommonService.setTypeFlag(csvRecord.get(7).trim(), csvRecord.get(9).trim(), csvRecord.get(11).trim()), //type_flag
                        "",      // Processed_by
                        "",            // processed_date
                        currentDateTime);
                apiBeftnModelList.add(apiBeftnModel);
            }
            return apiBeftnModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
