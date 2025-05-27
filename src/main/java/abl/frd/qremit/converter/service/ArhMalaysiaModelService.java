package abl.frd.qremit.converter.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ArhMalaysiaModel;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.ArhMalaysiaModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;

import org.apache.commons.csv.*;
import java.io.*;
import java.time.*;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class ArhMalaysiaModelService {
    @Autowired
    ArhMalaysiaModelRepository arhMalaysiaModelRepository;
    @Autowired
    FileInfoModelService fileInfoModelService;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    CommonService commonService;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    ErrorDataModelService errorDataModelService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String fileType, String nrtaCode, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            
            int type = 0;
            if(fileType.equalsIgnoreCase("API")) type = 1;
            Map<String, Object> agraniMalaysiaData = csvToAgraniMalaysiaModels(file.getInputStream(), type, user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<ArhMalaysiaModel> agraniMalaysiaModels = (List<ArhMalaysiaModel>) agraniMalaysiaData.get("agraniMalaysiaModelList");

            if(agraniMalaysiaData.containsKey("errorMessage")){
                resp.put("errorMessage", agraniMalaysiaData.get("errorMessage"));
            }
            if(agraniMalaysiaData.containsKey("errorCount") && ((Integer) agraniMalaysiaData.get("errorCount") >= 1)){
                int errorCount = (Integer) agraniMalaysiaData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(agraniMalaysiaModels.size()!=0) {
                for(ArhMalaysiaModel agraniMalaysiaModel: agraniMalaysiaModels){
                    agraniMalaysiaModel.setFileInfoModel(fileInfoModel);
                    agraniMalaysiaModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(agraniMalaysiaModels, fileInfoModel, user, currentDateTime, type);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(agraniMalaysiaModels.size()));
                fileInfoModel.setIsSettlement(type); 
                fileInfoModel.setAgraniMalaysiaModel(agraniMalaysiaModels);
   
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

    public Map<String, Object> csvToAgraniMalaysiaModels(InputStream is, int type, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, 
        LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<ArhMalaysiaModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ArhMalaysiaModel> agraniMalaysiaModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            int isValidFile = 1;
            for(CSVRecord csvRecord: csvRecords){
                i++;
                int length = csvRecord.size();
                if(i == 1){
                    Map<String, Object> apiCheckResp = checkAgraniMalaysiaApiOrBeftnData(csvRecord.get(0), length, type, nrtaCode, exchangeCode);
                    if((Integer) apiCheckResp.get("err") == 1){
                        resp.put("errorMessage", apiCheckResp.get("msg"));
                        isValidFile = 0;
                        break;
                    }
                }
                Map<String, Object> data = getCsvData(type, csvRecord, exchangeCode);
                if(type == 1){
                    String errorMessage = checkArhMalaysiaApiTransactionStatus(csvRecord.get(12).toLowerCase());
                    if(!errorMessage.isEmpty()){
                        CommonService.addErrorDataModelList(errorDataModelList, data, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            if(isValidFile == 1){
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
                modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, ArhMalaysiaModel.class, resp, errorDataModelList, fileExchangeCode, 1, type);
                agraniMalaysiaModelList = (List<ArhMalaysiaModel>) modelResp.get("modelList");
                errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
                duplicateMessage = modelResp.get("duplicateMessage").toString();
                duplicateCount = (int) modelResp.get("duplicateCount");
            }
             //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && agraniMalaysiaModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("agraniMalaysiaModelList", agraniMalaysiaModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        }catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> getCsvData(int type, CSVRecord csvRecord, String exchangeCode){
        String amount = csvRecord.get(3).trim();
        String bankCode = (type == 1) ? csvRecord.get(9).trim(): csvRecord.get(8).trim();
        String bankName = (type == 1) ? csvRecord.get(8).trim(): csvRecord.get(9).trim();
        String branchName = (type == 1) ? "Principal": csvRecord.get(10).trim();
        String branchCode = (type == 1) ? "4006": CommonService.fixRoutingNo(csvRecord.get(11).trim());
        LocalDateTime date = CommonService.convertStringToDate(csvRecord.get(4).trim());

        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", csvRecord.get(1).trim());
        data.put("currency", csvRecord.get(2).trim());
        data.put("amount", amount);
        data.put("enteredDate", date.toLocalDate().toString());
        data.put("remitterName", csvRecord.get(5).trim());
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(6).trim());
        data.put("beneficiaryAccount", csvRecord.get(7).trim());
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        String[] fields = {"remitterMobile","beneficiaryMobile","draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate"};
        for(String field: fields)   data.put(field, "");
        return data;
    }

    public Map<String, Object> checkAgraniMalaysiaApiOrBeftnData(String firstColumn, int length, int type, String nrtaCode, String exchangeCode){
        Map<String, Object> resp = CommonService.getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        if(type == 1 && (length != 13 || !firstColumn.equals(exchangeCode)))    resp = CommonService.getResp(1, msg, null);
        else if(type == 0 && (length != 12 || !firstColumn.equals(nrtaCode)))  resp = CommonService.getResp(1, msg, null);
        return resp;
    }

    public static String checkArhMalaysiaApiTransactionStatus(String status){
        String errorMessage = "";
        if(status.startsWith("status"))  errorMessage = "A/C Not Credited from API";
        return errorMessage;
    }
}
