package abl.frd.qremit.converter.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.AgraniMalaysiaModel;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AgraniMalaysiaModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;

import org.apache.commons.csv.*;
import java.io.*;
import java.time.*;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class AgraniMalaysiaModelService {
    @Autowired
    AgraniMalaysiaModelRepository agraniMalaysiaModelRepository;
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
            List<AgraniMalaysiaModel> agraniMalaysiaModels = (List<AgraniMalaysiaModel>) agraniMalaysiaData.get("agraniMalaysiaModelList");

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
                for(AgraniMalaysiaModel agraniMalaysiaModel: agraniMalaysiaModels){
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
        Optional<AgraniMalaysiaModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<AgraniMalaysiaModel> agraniMalaysiaModelList = new ArrayList<>();
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
                    Map<String, Object> apiCheckResp = checkAgraniMalaysiaApiOrBeftnData(csvRecord.get(0), length, type, nrtaCode);
                    if((Integer) apiCheckResp.get("err") == 1){
                        resp.put("errorMessage", apiCheckResp.get("msg"));
                        isValidFile = 0;
                        break;
                    }
                }
                Map<String, Object> data = getCsvData(type, csvRecord, exchangeCode);
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
                modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, AgraniMalaysiaModel.class, resp, errorDataModelList, fileExchangeCode, 1, type);
                agraniMalaysiaModelList = (List<AgraniMalaysiaModel>) modelResp.get("modelList");
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
        String transactionNo = (type == 1)  ?   csvRecord.get(0):csvRecord.get(1);
        String amount = csvRecord.get(3).trim();
        String bankCode = (type == 1) ? "11": csvRecord.get(8).trim();
        String bankName = (type == 1) ? "Agrani Bank": csvRecord.get(9).trim();
        String branchName = (type == 1) ? "Principal": csvRecord.get(10).trim();
        String branchCode = (type == 1) ? "4006": CommonService.fixRoutingNo(csvRecord.get(11).trim());
        String currrency = (type == 1) ? "BDT": csvRecord.get(2);
        String remiterName = (type == 1) ? csvRecord.get(2) : csvRecord.get(5);
        String beneficiaryName = (type == 1) ? csvRecord.get(5):csvRecord.get(6);
        String beneficiaryAccount = (type == 1) ? csvRecord.get(6): csvRecord.get(7);

        //String format = (type == 1) ? "yyyy-MM-dd HH:mm:ss Z":"yyyy-MM-dd'T'HH:mm:ss.SSS";
        String enteredDate = (type == 1) ? csvRecord.get(2): csvRecord.get(4);
        //LocalDate date = CommonService.convertStringToLocalDate(enteredDate,format);
        LocalDateTime date = CommonService.convertStringToDate(enteredDate);

        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo.trim());
        data.put("currency", currrency);
        data.put("amount", amount);
        data.put("enteredDate", date.toLocalDate().toString());
        data.put("remitterName", remiterName.trim());
        data.put("remitterMobile", "");
        data.put("beneficiaryName", beneficiaryName.trim());
        data.put("beneficiaryAccount", beneficiaryAccount.trim());
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        String[] fields = {"remitterMobile","beneficiaryMobile","draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate"};
        for(String field: fields)   data.put(field, "");
        return data;
    }

    public Map<String, Object> checkAgraniMalaysiaApiOrBeftnData(String firstColumn, int length, int type, String nrtaCode){
        Map<String, Object> resp = CommonService.getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        if(type == 1 && length != 15)    resp = CommonService.getResp(1, msg, null);
        else if(type == 0 && length != 12 && !firstColumn.equals(nrtaCode))  resp = CommonService.getResp(1, msg, null);
        return resp;
    }
}
