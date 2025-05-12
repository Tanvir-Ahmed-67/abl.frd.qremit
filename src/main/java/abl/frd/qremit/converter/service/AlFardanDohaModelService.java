package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.AlFardanDohaModel;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.AlFardanDohaModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class AlFardanDohaModelService {
    @Autowired
    AlFardanDohaModelRepository alFardanDohaModelRepository;
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
    CommonService commonService;
    @Autowired
    CustomQueryService customQueryService;
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode, String tbl) {
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

            Map<String, Object> alFardanDohaData = csvToAlFardanDohaModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<AlFardanDohaModel> alFardanDohaModels = (List<AlFardanDohaModel>) alFardanDohaData.get("alFardanDohaModelList");

            if(alFardanDohaData.containsKey("errorMessage")){
                resp.put("errorMessage", alFardanDohaData.get("errorMessage"));
            }
            if(alFardanDohaData.containsKey("errorCount") && ((Integer) alFardanDohaData.get("errorCount") >= 1)){
                int errorCount = (Integer) alFardanDohaData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            if(alFardanDohaModels.size()!=0) {
                for (AlFardanDohaModel alFardanDohaModel : alFardanDohaModels) {
                    alFardanDohaModel.setFileInfoModel(fileInfoModel);
                    alFardanDohaModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(alFardanDohaModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(alFardanDohaModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setAlFardanDohaModel(alFardanDohaModels);
   
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
    public Map<String, Object> csvToAlFardanDohaModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<AlFardanDohaModel> duplicateData = Optional.empty();
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
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = csvRecord.get(0).trim(); 
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, AlFardanDohaModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<AlFardanDohaModel> alFardanDohaModelList = (List<AlFardanDohaModel>) modelResp.get("modelList");
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
            if(errorDataModelList.isEmpty() && alFardanDohaModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("alFardanDohaModelList", alFardanDohaModelList);
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
        LocalDate date = CommonService.convertStringToLocalDate(csvRecord.get(4), "dd/MM/yyyy");
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
}
