package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.RiaModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class RiaModelService {
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
    RiaModelRepository riaModelRepository;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    CustomQueryService customQueryService;
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
            Map<String, Object> riaData = csvToRiaModels(file.getInputStream(), type, exchangeCode, user, fileInfoModel, nrtaCode, currentDateTime, tbl);
            List<RiaModel> riaModelList = (List<RiaModel>) riaData.get("riaModelList");
            if(riaData.containsKey("errorMessage")){
                resp.put("errorMessage", riaData.get("errorMessage"));
            }
            if(riaData.containsKey("errorCount") && ((Integer) riaData.get("errorCount") >= 1)){
                int errorCount = (Integer) riaData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            if(riaModelList.size()!=0) {
                for (RiaModel riaModel : riaModelList) {
                    riaModel.setFileInfoModel(fileInfoModel);
                    riaModel.setUserModel(user);
                }
                // 4 DIFFERENT DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(riaModelList, fileInfoModel, user, currentDateTime, type);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(riaModelList.size()));
                fileInfoModel.setIsSettlement(type);
                fileInfoModel.setRiaModel(riaModelList);
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
    public Map<String, Object> csvToRiaModels(InputStream is, int type, String exchangeCode, User user, FileInfoModel fileInfoModel, String nrtaCode, 
        LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<RiaModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<RiaModel> riaModelList = new ArrayList<>();
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
                if(i == 1){
                    Map<String, Object> apiCheckResp = checkRiaApiOrBeftnData(csvRecord.get(0), type);
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
                String beneficiaryAccount = csvRecord.get(7).trim();
                String amount = (type == 1) ? csvRecord.get(1) : csvRecord.get(3);
                Map<String, Object> data = getCsvData(csvRecord, type, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode, amount);
                //check api error for ria special case
                if(type == 1){
                    String errorStatus = csvRecord.get(8).toLowerCase();
                    if(errorStatus.startsWith("error") || errorStatus.startsWith("cancel")){
                        String errorMessage = "Cancelled From API";
                        CommonService.addErrorDataModelList(errorDataModelList, data, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
                        continue;
                    }
                }
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            if(isValidFile == 1){    
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
                modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, RiaModel.class, resp, errorDataModelList, fileExchangeCode, 1, type);
                riaModelList = (List<RiaModel>) modelResp.get("modelList");
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
            if(errorDataModelList.isEmpty() && riaModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("riaModelList", riaModelList);
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

    public Map<String, Object> getCsvData(CSVRecord csvRecord, int type, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode, String amount){
        Map<String, Object> data = new HashMap<>();
        String bankCode = (type == 1) ? "11": csvRecord.get(8).trim();
        String branchName = (type == 1) ? "": csvRecord.get(10).trim();
        String currrency = (type == 1) ? "BDT": csvRecord.get(2);
        String enteredDate = (type == 1) ? csvRecord.get(10) : csvRecord.get(4);
        String remiterName = (type == 1) ? csvRecord.get(3) : csvRecord.get(5);
        if(type == 0){
            LocalDateTime date = CommonService.convertStringToDate(enteredDate);
            enteredDate = date.toLocalDate().toString();
        }
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
    
    public Map<String, Object> checkRiaApiOrBeftnData(String firstColumn, int type){
        Map<String, Object> resp = CommonService.getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        if(type == 0 && !firstColumn.equals("7081")) resp = CommonService.getResp(1, msg, null);
        else if(type == 1 && firstColumn.equals("7081"))   resp = CommonService.getResp(1, msg, null);
        return resp;
    }
}