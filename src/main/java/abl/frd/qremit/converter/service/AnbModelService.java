package abl.frd.qremit.converter.service;
import java.io.*;
import java.time.LocalDateTime;
import abl.frd.qremit.converter.model.AnbModel;
import abl.frd.qremit.converter.repository.AnbModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class AnbModelService {
    @Autowired
    AnbModelRepository anbModelRepository;
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

            Map<String, Object> anbData = csvToAnbModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);
            List<AnbModel> anbModels = (List<AnbModel>) anbData.get("anbModelList");

            if(anbData.containsKey("errorMessage")){
                resp.put("errorMessage", anbData.get("errorMessage"));
            }
            if(anbData.containsKey("errorCount") && ((Integer) anbData.get("errorCount") >= 1)){
                int errorCount = (Integer) anbData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            if(anbModels.size()!=0) {
                for (AnbModel anbModel : anbModels) {
                    anbModel.setFileInfoModel(fileInfoModel);
                    anbModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(anbModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(anbModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setAnbModel(anbModels);
   
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
    public Map<String, Object> csvToAnbModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<AnbModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|') .withIgnoreHeaderCase().withTrim())) {
            //skip first line
            fileReader.readLine();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            int i = 0;
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                String transactionNo = csvRecord.get(4).trim();
                String amount = getAmount(csvRecord.get(14).trim());
                String bankName = csvRecord.get(24).trim();
                String branchCode = CommonService.fixRoutingNo(csvRecord.get(22).trim());
                String beneficiaryAccount = getBenificiaryAccount(csvRecord, branchCode, bankName);
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode, amount);
                data.put("nrtaCode", nrtaCode);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }

            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, AnbModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<AnbModel> anbModelList = (List<AnbModel>) modelResp.get("modelList");
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
           if(errorDataModelList.isEmpty() && anbModelList.isEmpty()){
               fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
           }
           resp.put("anbModelList", anbModelList);
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

    public String getBenificiaryAccount(CSVRecord csvRecord, String branchCode, String bankName){
        String benificiaryAccount = csvRecord.get(25).trim();
        String mobile = csvRecord.get(20).trim();
        if(("101").equals(csvRecord.get(8).trim())){
            mobile = mobile.replaceFirst("^0+", "");
            benificiaryAccount = "COC" + mobile;
        }
        return benificiaryAccount;
    }

    public String getAmount(String amountStr){
        Double amount = CommonService.convertStringToDouble(amountStr);
        amount = amount/100;
        return String.valueOf(amount);
    }

    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode, String amount){
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", "BDT");
        data.put("amount", amount);
        data.put("enteredDate", csvRecord.get(6).replace("/", "-"));
        data.put("remitterName", csvRecord.get(10));
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(11));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(20));
        data.put("bankName", bankName);
        data.put("bankCode", "");
        data.put("branchName", csvRecord.get(23));
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
