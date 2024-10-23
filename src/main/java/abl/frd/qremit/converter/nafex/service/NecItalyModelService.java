package abl.frd.qremit.converter.nafex.service;

import java.io.*;
import java.time.LocalDateTime;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.NecItalyModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.NecItalyModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class NecItalyModelService {
    @Autowired
    NecItalyModelRepository necItalyModelRepository;
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
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    LocalDateTime currentDateTime = LocalDateTime.now();
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode) {
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

            Map<String, Object> necItalyData = csvToNecItalyModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode);
            List<NecItalyModel> necItalyModels = (List<NecItalyModel>) necItalyData.get("necItalyDataModelList");

            if(necItalyData.containsKey("errorMessage")){
                resp.put("errorMessage", necItalyData.get("errorMessage"));
            }
            if(necItalyData.containsKey("errorCount") && ((Integer) necItalyData.get("errorCount") >= 1)){
                int errorCount = (Integer) necItalyData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(necItalyModels.size()!=0) {
                for(NecItalyModel necItalyModel : necItalyModels){
                    necItalyModel.setFileInfoModel(fileInfoModel);
                    necItalyModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(necItalyModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(necItalyModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setNecItalyModel(necItalyModels);
   
                // SAVING TO MySql Data Table
                try{
                    fileInfoModelRepository.save(fileInfoModel);                
                    resp.put("fileInfoModel", fileInfoModel);
                }catch(Exception e){
                    resp.put("errorMessage", e.getMessage());
                }
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> csvToNecItalyModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode) {
        Map<String, Object> resp = new HashMap<>();
        Optional<NecItalyModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<NecItalyModel> necItalyDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                duplicateData = necItalyModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
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

                NecItalyModel necItalyDataModel = new NecItalyModel();
                necItalyDataModel = CommonService.createDataModel(necItalyDataModel, data);
                necItalyDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                necItalyDataModel.setUploadDateTime(currentDateTime);
                necItalyDataModelList.add(necItalyDataModel);
            }

            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && necItalyDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("necItalyDataModelList", necItalyDataModelList);
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
        data.put("remitterMobile", csvRecord.get(17));
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(12));
        data.put("bankName", bankName);
        data.put("bankCode", csvRecord.get(9));
        data.put("branchName", csvRecord.get(10));
        data.put("branchCode", branchCode);
        data.put("draweeBranchName", csvRecord.get(13));
        data.put("draweeBranchCode", csvRecord.get(14));
        data.put("purposeOfRemittance", csvRecord.get(15));
        data.put("sourceOfIncome", csvRecord.get(16));
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }


}
