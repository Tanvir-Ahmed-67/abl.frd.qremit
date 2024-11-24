package abl.frd.qremit.converter.nafex.service;
import java.io.*;
import java.time.LocalDateTime;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.AgraniMalaysiaModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.AgraniMalaysiaModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
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
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String fileType, String nrtaCode) {
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
            if(type == 1)   agraniMalaysiaData = csvToAgraniMalaysiaModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);
            else    agraniMalaysiaData = beftnToAgraniMalaysiaModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);

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
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(agraniMalaysiaModels, fileInfoModel, user, currentDateTime, type);
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

    public Map<String, Object> csvToAgraniMalaysiaModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime) {
        Map<String, Object> resp = new HashMap<>();
        Optional<AgraniMalaysiaModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<AgraniMalaysiaModel> agraniMalaysiaDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                duplicateData = agraniMalaysiaModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                String beneficiaryAccount = csvRecord.get(7).trim();
                String bankName = csvRecord.get(8).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                if(CommonService.checkAgraniBankName(bankName) && beneficiaryAccount.isEmpty()) beneficiaryAccount = "Account to be opened";
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

                AgraniMalaysiaModel agraniMalaysiaDataModel = new AgraniMalaysiaModel();
                agraniMalaysiaDataModel = CommonService.createDataModel(agraniMalaysiaDataModel, data);
                agraniMalaysiaDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                agraniMalaysiaDataModel.setUploadDateTime(currentDateTime);
                agraniMalaysiaDataModelList.add(agraniMalaysiaDataModel);
            }

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

    public Map<String, Object> beftnToAgraniMalaysiaModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime){
        Map<String, Object> resp = new HashMap<>();
        Optional<AgraniMalaysiaModel> duplicateData;
        try{
            Workbook records = CommonService.getWorkbook(is);
            Row row;
            Sheet worksheet = records.getSheetAt(0);
            List<AgraniMalaysiaModel> agraniMalaysiaDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (int rowIndex = 2; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                row = worksheet.getRow(rowIndex);
                if(row == null) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                i++;
                Map<String, Object> data = getBeftnData(row, exchangeCode);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                String bankName = data.get("bankName").toString();
                String branchCode = data.get("branchCode").toString();
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

                AgraniMalaysiaModel agraniMalaysiaDataModel = new AgraniMalaysiaModel();
                agraniMalaysiaDataModel = CommonService.createDataModel(agraniMalaysiaDataModel, data);
                agraniMalaysiaDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                agraniMalaysiaDataModel.setUploadDateTime(currentDateTime);
                agraniMalaysiaDataModelList.add(agraniMalaysiaDataModel);
            }
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

        return data;
    }
}
