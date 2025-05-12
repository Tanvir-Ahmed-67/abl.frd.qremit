package abl.frd.qremit.converter.service;
import java.io.*;
import java.time.*;

import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.AgraniMalaysiaModel;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.AgraniMalaysiaModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class AgraniMalaysiaModelService {
    @Autowired
    AgraniMalaysiaModelRepository agraniMalaysiaModelRepository;
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
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
    
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
            Map<String, Object> agraniMalaysiaData = new HashMap<>();
            if(type == 1)   agraniMalaysiaData = csvToAgraniMalaysiaModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            else    agraniMalaysiaData = beftnToAgraniMalaysiaModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);

            List<AgraniMalaysiaModel> agraniMalaysiaModels = (List<AgraniMalaysiaModel>) agraniMalaysiaData.get("agraniMalaysiaDataModelList");

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
                for(AgraniMalaysiaModel agraniMalaysiaModel : agraniMalaysiaModels){
                    agraniMalaysiaModel.setFileInfoModel(fileInfoModel);
                    agraniMalaysiaModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                //Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(agraniMalaysiaModels, fileInfoModel, user, currentDateTime, 0);
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(agraniMalaysiaModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(agraniMalaysiaModels.size()));
                fileInfoModel.setIsSettlement(0);
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

    public Map<String, Object> csvToAgraniMalaysiaModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<AgraniMalaysiaModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            int i = 0;
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                if(CommonService.checkAgraniBankName(bankName) && beneficiaryAccount.isEmpty()) beneficiaryAccount = "Account to be opened";
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = csvRecord.get(0).trim();   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, AgraniMalaysiaModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<AgraniMalaysiaModel> agraniMalaysiaDataModelList = (List<AgraniMalaysiaModel>) modelResp.get("modelList");
            errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
            String duplicateMessage = modelResp.get("duplicateMessage").toString();
            int duplicateCount = (int) modelResp.get("duplicateCount");

            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && agraniMalaysiaDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("agraniMalaysiaDataModelList", agraniMalaysiaDataModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode){
        Map<String, Object> data = new HashMap<>();
        LocalDate date = CommonService.convertStringToLocalDate(csvRecord.get(4), "MM/dd/yyyy");
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", CommonService.convertLocalDateToString(date));
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

    public Map<String, Object> beftnToAgraniMalaysiaModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<AgraniMalaysiaModel> duplicateData = Optional.empty();
        try{
            Workbook records = CommonService.getWorkbook(is);
            Row row;
            Sheet worksheet = records.getSheetAt(0);
            int i = 0;
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (int rowIndex = 2; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                row = worksheet.getRow(rowIndex);
                if(row == null) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                i++;
                Map<String, Object> data = getBeftnData(row, exchangeCode);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
                /*
                duplicateData = agraniMalaysiaModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, nrtaCode, duplicateData, transactionList);
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
                String typeFlag = CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode);
                if(!CommonService.convertStringToInt(typeFlag).equals(3)){
                    String msg = "Invalid Remittence Type for BEFTN";
                    CommonService.addErrorDataModelList(errorDataModelList, data, exchangeCode, msg, currentDateTime, user, fileInfoModel);
                    continue;
                }
                AgraniMalaysiaModel agraniMalaysiaDataModel = new AgraniMalaysiaModel();
                agraniMalaysiaDataModel = CommonService.createDataModel(agraniMalaysiaDataModel, data);
                agraniMalaysiaDataModel.setTypeFlag(typeFlag);
                agraniMalaysiaDataModel.setUploadDateTime(currentDateTime);
                agraniMalaysiaDataModelList.add(agraniMalaysiaDataModel);
            
            }
            */
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, AgraniMalaysiaModel.class, resp, errorDataModelList, fileExchangeCode, 1, 3);
            List<AgraniMalaysiaModel> agraniMalaysiaDataModelList = (List<AgraniMalaysiaModel>) modelResp.get("modelList");
            errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
            String duplicateMessage = modelResp.get("duplicateMessage").toString();
            int duplicateCount = (int) modelResp.get("duplicateCount");
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && agraniMalaysiaDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("agraniMalaysiaDataModelList", agraniMalaysiaDataModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        }catch(IOException e){
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    
    public Map<String, Object> getBeftnData(Row row, String exchangeCode){
        Map<String, Object> data = new HashMap<>();
        String[] fields = {"beneficiaryMobile","draweeBranchName","draweeBranchCode","purposeOfRemittance","sourceOfIncome","processFlag","processedBy","processedDate","remitterMobile"};
        String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(10)));
        String amount = CommonService.getCellValueAsString(row.getCell(11));
        String bankName = "";
        String bankCode = "";
        String branchName = "";
        if(!branchCode.isEmpty()){
            Map<String, Object> routingDetails = customQueryService.getRoutingDetailsByRoutingNo(branchCode);
            if(!routingDetails.isEmpty()){
                bankName = routingDetails.get("bank_name").toString();
                bankCode = routingDetails.get("bank_code").toString();
                branchName = routingDetails.get("branch_name").toString();
            }
        }
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", CommonService.getCellValueAsString(row.getCell(5)));
        data.put("currency", "BDT");
        data.put("amount", amount);
        data.put("enteredDate", CommonService.getCurrentDate("yyyy-MM-dd"));
        data.put("remitterName", "");
        data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(7)));
        data.put("beneficiaryAccount", CommonService.getCellValueAsString(row.getCell(8)));
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        for(String field: fields)   data.put(field, "");

        /*
        String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(8)));
        Map<String, Object> routingDetails = customQueryService.getRoutingDetailsByRoutingNo(branchCode);
        String bankName = "";
        String bankCode = "";
        String branchName = "";
        String amount = CommonService.getCellValueAsString(row.getCell(10));

        if(!routingDetails.isEmpty()){
            bankName = routingDetails.get("bank_name").toString();
            bankCode = routingDetails.get("bank_code").toString();
            branchName = routingDetails.get("branch_name").toString();
        }
        
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", CommonService.getCellValueAsString(row.getCell(0)));
        data.put("currency", CommonService.getCellValueAsString(row.getCell(9)));
        data.put("amount", amount);
        data.put("enteredDate", CommonService.getCurrentDate("MM/dd/yyyy"));
        data.put("remitterName", CommonService.getCellValueAsString(row.getCell(2)));
        data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(5)));
        data.put("beneficiaryAccount", CommonService.getCellValueAsString(row.getCell(6)));
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        for(String field: fields)   data.put(field, "");
        */
        return data;
    }
}
