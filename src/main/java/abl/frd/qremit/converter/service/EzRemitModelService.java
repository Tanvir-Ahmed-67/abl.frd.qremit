package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.EzRemitModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.*;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class EzRemitModelService {
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
    EzRemitModelRepository ezRemitModelRepository;
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
            Map<String, Object> ezRemitData = csvToEzRemitModels(file.getInputStream(), type, user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<EzRemitModel> ezRemitModelList = (List<EzRemitModel>) ezRemitData.get("ezRemitModelList");
            if(ezRemitData.containsKey("errorMessage")){
                resp.put("errorMessage", ezRemitData.get("errorMessage"));
            }
            if(ezRemitData.containsKey("errorCount") && ((Integer) ezRemitData.get("errorCount") >= 1)){
                int errorCount = (Integer) ezRemitData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            
            if(ezRemitModelList.size()!=0) {
                for (EzRemitModel ezRemitModel : ezRemitModelList) {
                    ezRemitModel.setFileInfoModel(fileInfoModel);
                    ezRemitModel.setUserModel(user);
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(ezRemitModelList, fileInfoModel, user, currentDateTime, type);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(ezRemitModelList.size()));
                fileInfoModel.setIsSettlement(type);
                fileInfoModel.setEzRemitModel(ezRemitModelList);
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

    public Map<String, Object> csvToEzRemitModels(InputStream is, int type, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<EzRemitModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<EzRemitModel> ezRemitModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            int isValidFile = 1;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                int length = csvRecord.size();
                if(i == 1){
                    Map<String, Object> apiCheckResp = checkEzRemitApiOrBeftnData(length, type);
                    if((Integer) apiCheckResp.get("err") == 1){
                        resp.put("errorMessage", apiCheckResp.get("msg"));
                        isValidFile = 0;
                        break;
                    }
                }
                String bankName = (type == 1) ? "Agrani Bank": csvRecord.get(9).trim();
                String branchCode = (type == 1) ? "": csvRecord.get(11).trim();
                branchCode = CommonService.fixRoutingNo(branchCode);
                String transactionNo = (type == 1) ? csvRecord.get(0).trim(): csvRecord.get(1).trim();
                String beneficiaryAccount = (type == 1) ? csvRecord.get(4).trim(): csvRecord.get(7).trim();
                String amount = (type == 1) ? csvRecord.get(5) : csvRecord.get(3);
                Map<String, Object> data = getCsvData(csvRecord, type, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode, amount);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            if(isValidFile == 1){
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
                modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, EzRemitModel.class, resp, fileExchangeCode, 1, type);
                ezRemitModelList = (List<EzRemitModel>) modelResp.get("modelList");
                errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
                duplicateMessage = modelResp.get("duplicateMessage").toString();
                duplicateCount = (int) modelResp.get("duplicateCount");
                /*
                for(Map<String, Object> data: dataList){
                    String transactionNo = data.get("transactionNo").toString();
                    String bankName = data.get("bankName").toString();
                    String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                    String branchCode = data.get("branchCode").toString();
                    Map<String, Object> dupResp = CommonService.getDuplicateTransactionNo(transactionNo, uniqueDataList);
                    if((Integer) dupResp.get("isDuplicate") == 1){
                        duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                        duplicateCount++;
                        continue;
                    }
                    Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, fileExchangeCode, duplicateData, transactionList);
                    if((Integer) errResp.get("err") == 1){
                        errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                        continue;
                    }
                    if((Integer) errResp.get("err") == 2){
                        resp.put("errorMessage", errResp.get("msg"));
                        break;
                    }
                    if((Integer) errResp.get("err") == 4){
                        duplicateMessage += errResp.get("msg");
                        continue;
                    }
                    if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");
                    String typeFlag = CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode);
                    int allowedType = (type == 1) ? 1:3;  //for betn 3
                    if(!CommonService.convertStringToInt(typeFlag).equals(allowedType)){
                        String msg = "Invalid Remittence Type for ";
                        msg += (type == 1) ? "API": "BEFTN"; 
                        CommonService.addErrorDataModelList(errorDataModelList, data, exchangeCode, msg, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    EzRemitModel ezRemitModel = new EzRemitModel();
                    ezRemitModel = CommonService.createDataModel(ezRemitModel, data);
                    ezRemitModel.setTypeFlag(typeFlag);
                    ezRemitModel.setUploadDateTime(currentDateTime);
                    ezRemitModelList.add(ezRemitModel);
                }
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
            if(errorDataModelList.isEmpty() && ezRemitModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("ezRemitModelList", ezRemitModelList);
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

    public Map<String, Object> getCsvData(CSVRecord csvRecord, int type, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode, String amount){
        String bankCode = (type == 1) ? "11": csvRecord.get(8).trim();
        String branchName = (type == 1) ? "": csvRecord.get(10).trim();
        String currrency = (type == 1) ? "BDT": csvRecord.get(2);
        //String amount = (type == 1) ? csvRecord.get(5) : csvRecord.get(3);
        String enteredDate = (type == 1) ? csvRecord.get(7) : csvRecord.get(4);
        String remiterName = (type == 1) ? csvRecord.get(1) : csvRecord.get(5);

        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", currrency);
        data.put("amount", amount);
        data.put("enteredDate", enteredDate);
        data.put("remitterName", remiterName);
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", "");
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
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

    public Map<String, Object> checkEzRemitApiOrBeftnData(int length, int type){
        Map<String, Object> resp = CommonService.getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        if(type == 1 && length != 8)    resp = CommonService.getResp(1, msg, null);
        else if(type == 0 && length != 12)  resp = CommonService.getResp(1, msg, null);
        return resp;
    }
}