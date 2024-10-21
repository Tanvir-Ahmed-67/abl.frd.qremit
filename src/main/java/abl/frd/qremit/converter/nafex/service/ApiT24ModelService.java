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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@SuppressWarnings("unchecked")
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
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    LocalDateTime currentDateTime = LocalDateTime.now();

    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode) {
        Map<String, Object> resp = new HashMap<>();
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);

            //List<ApiT24Model> apiT24Models = csvToApiT24Models(file.getInputStream());
            Map<String, Object> apiT24Data= csvToApiT24Models(file.getInputStream(), user, fileInfoModel);
            List<ApiT24Model> apiT24Models = (List<ApiT24Model>) apiT24Data.get("apiT24ModelList");
            if(apiT24Data.containsKey("errorMessage")){
                resp.put("errorMessage", apiT24Data.get("errorMessage"));
            }
            if(apiT24Data.containsKey("errorCount") && ((Integer) apiT24Data.get("errorCount") >= 1)){
                int errorCount = (Integer) apiT24Data.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(apiT24Models.size()!=0) {
                for(ApiT24Model apiT24Model : apiT24Models){
                    apiT24Model.setFileInfoModel(fileInfoModel);
                    apiT24Model.setUserModel(user);
                }

                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(apiT24Models, fileInfoModel, user, currentDateTime, 1);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(apiT24Models.size()));
                fileInfoModel.setIsSettlement(1);
                fileInfoModel.setApiT24Model(apiT24Models);
                try{
                    fileInfoModelRepository.save(fileInfoModel);                
                    resp.put("fileInfoModel", fileInfoModel);
                }catch(Exception e){
                    resp.put("errorMessage", e.getMessage());
                }

                /*
                // Online DATA TABLE GENERATION GOING ON HERE
                //List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(apiT24Models,"getCheckT24", "1", currentDateTime);
                List<OnlineModel> onlineModelList = CommonService.generateOnlineModelList(apiT24Models, currentDateTime, 1);

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
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
        return resp;
    }
    public Map<String, Object> csvToApiT24Models(InputStream is, User user, FileInfoModel fileInfoModel) {
        Map<String, Object> resp = new HashMap<>();
        Optional<ApiT24Model> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
            Map<String, String> nrtaCodeVsExchangeCodeMap = CommonService.getNrtaCodeVsExchangeCodeMap(exchangeHouseModelList);
            List<ApiT24Model> apiT24ModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                duplicateData = apiT24ModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                String nrtaCode = csvRecord.get(0);
                String exchangeCode = nrtaCodeVsExchangeCodeMap.get(nrtaCode);
                String bankName = csvRecord.get(8);
                String beneficiaryAccount = csvRecord.get(7).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                String transactionNo = csvRecord.get(1).trim();
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
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
                ApiT24Model apiT24Model = new ApiT24Model();
                apiT24Model = CommonService.createDataModel(apiT24Model, data);
                apiT24Model.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                apiT24Model.setUploadDateTime(currentDateTime);
                apiT24ModelList.add(apiT24Model);
                /*
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
                        "", //remitterMobile
                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        "", //beneficiaryMobile
                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        "", //draweeBranchName
                        "", //draweeBranchCode
                        "", //purposeOfRemittance
                        "", //sourceOfIncome
                        "",    // processed_flag
                        CommonService.setTypeFlag(csvRecord.get(7).trim(), csvRecord.get(8).trim(), csvRecord.get(11).trim()), //type_flag
                        "",      // Processed_by
                        "",            // processed_date
                        currentDateTime);
                apiT24ModelList.add(apiT24Model);
                */
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && apiT24ModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("apiT24ModelList", apiT24ModelList);
            resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode){
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", csvRecord.get(4));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", "");
        data.put("bankName", bankName);
        data.put("bankCode", csvRecord.get(9));
        data.put("branchName", csvRecord.get(10));
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
}
