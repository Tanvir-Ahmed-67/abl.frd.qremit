package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.*;
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
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String tbl) {
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

            //List<ApiT24Model> apiT24Models = csvToApiT24Models(file.getInputStream());
            Map<String, Object> apiT24Data= csvToApiT24Models(file.getInputStream(), user, fileInfoModel, currentDateTime, tbl);
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
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(apiT24Models, fileInfoModel, user, currentDateTime, 1);
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
            }
            
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
        return resp;
    }
    public Map<String, Object> csvToApiT24Models(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<ApiT24Model> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
            Map<String, String> nrtaCodeVsExchangeCodeMap = CommonService.getNrtaCodeVsExchangeCodeMap(exchangeHouseModelList);
            //List<ApiT24Model> apiT24ModelList = new ArrayList<>();
            //List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            //List<String> transactionList = new ArrayList<>();
            //String duplicateMessage = "";
            int i = 0;
            //int duplicateCount = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            int isValidFile = 1;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String nrtaCode = csvRecord.get(0);
                String exchangeCode = nrtaCodeVsExchangeCodeMap.get(nrtaCode);
                String transactionNo = csvRecord.get(1).trim();
                String amount = csvRecord.get(3).trim();
                String bankName = csvRecord.get(8);
                String bankCode = csvRecord.get(9).trim();
                if(i == 1){
                    Map<String, Object> apiCheckResp = CommonService.checkApiOrBeftnData(bankCode, 1);
                    if((Integer) apiCheckResp.get("err") == 1){
                        resp.put("errorMessage", apiCheckResp.get("msg"));
                        isValidFile = 0;
                        break;
                    }
                }
                
                String beneficiaryAccount = csvRecord.get(7).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                data.put("nrtaCode", nrtaCode);
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            if(isValidFile == 1){
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
                modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, ApiT24Model.class, resp, "", 1, 1);
                /*
                for(Map<String, Object> data: dataList){
                    String transactionNo = data.get("transactionNo").toString();
                    String exchangeCode = data.get("exchangeCode").toString();
                    String nrtaCode = data.get("nrtaCode").toString();
                    String bankName = data.get("bankName").toString();
                    String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                    String branchCode = data.get("branchCode").toString();
                    data.remove("nrtaCode");
                    Map<String, Object> dupResp = CommonService.getDuplicateTransactionNo(transactionNo, uniqueDataList);
                    if((Integer) dupResp.get("isDuplicate") == 1){
                        duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                        duplicateCount++;
                        continue;
                    }
                    Map<String, Object> archiveResp = CommonService.getDuplicateTransactionNo(transactionNo, archiveDataList);
                    if((Integer) archiveResp.get("isDuplicate") == 1){
                        duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                        duplicateCount++;
                        continue;
                    }
                
                    Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, exchangeCode, duplicateData, transactionList);
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
                    if(!CommonService.convertStringToInt(typeFlag).equals(1)){
                        String msg = "Invalid Remittence Type for API";
                        CommonService.addErrorDataModelList(errorDataModelList, data, exchangeCode, msg, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                    ApiT24Model apiT24Model = new ApiT24Model();
                    apiT24Model = CommonService.createDataModel(apiT24Model, data);
                    apiT24Model.setTypeFlag(typeFlag);
                    apiT24Model.setUploadDateTime(currentDateTime);
                    apiT24ModelList.add(apiT24Model);
                    
                }
                    */
            }
            
            List<ApiT24Model> apiT24ModelList = (List<ApiT24Model>) modelResp.get("modelList");
            List<ErrorDataModel> errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
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
            if(errorDataModelList.isEmpty() && apiT24ModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("apiT24ModelList", apiT24ModelList);
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
