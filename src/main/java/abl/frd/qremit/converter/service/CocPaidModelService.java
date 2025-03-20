package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.CocPaidModel;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.CocPaidModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.transaction.Transactional;
@SuppressWarnings("unchecked")
@Service
public class CocPaidModelService {
    @Autowired
    CocPaidModelRepository cocPaidModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);

            Map<String, Object> cocPaidData = csvToCocPaidModels(file.getInputStream(), user, fileInfoModel, currentDateTime);
            List<CocPaidModel> cocPaidModels = (List<CocPaidModel>) cocPaidData.get("cocPaidModelList");
            if(cocPaidData.containsKey("errorMessage")){
                resp.put("errorMessage", cocPaidData.get("errorMessage"));
            }
            if(cocPaidData.containsKey("errorCount") && ((Integer) cocPaidData.get("errorCount") >= 1)){
                int errorCount = (Integer) cocPaidData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            
            if(cocPaidModels.size() != 0){
                int totalCount = cocPaidModels.size();
                fileInfoModel.setIsSettlement(1);
                fileInfoModel.setTotalCount(String.valueOf(totalCount));
                fileInfoModel.setCocPaidModelList(cocPaidModels);
                fileInfoModel.setCocCount(String.valueOf(totalCount));
                fileInfoModel.setAccountPayeeCount("0");
                fileInfoModel.setOnlineCount("0");
                fileInfoModel.setBeftnCount("0");
                Double totalAmount = (cocPaidData.containsKey("totalAmount")) ?  CommonService.convertStringToDouble(cocPaidData.get("totalAmount").toString()): 0.0;
                String totalAmountStr = CommonService.convertNumberFormat(totalAmount, 2);
                fileInfoModel.setTotalAmount(totalAmountStr);
            }
            // SAVING TO MySql Data Table
            try{
                fileInfoModelRepository.save(fileInfoModel);                
                resp.put("fileInfoModel", fileInfoModel);
            }catch(Exception e){
                resp.put("errorMessage", e.getMessage());
            }

        }
        catch(Exception e){
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    public Map<String, Object> csvToCocPaidModels(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime) {
        Map<String, Object> resp = new HashMap<>();
        Optional<CocPaidModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<CocPaidModel> cocPaidModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            Double totalAmount = 0.0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                Map<String, Object> data = getCsvData(csvRecord);
                String transactionNo = data.get("transactionNo").toString();
                String amountStr = data.get("amount").toString();
                Double amount = CommonService.convertStringToDouble(amountStr);
                String exchangeCode = data.get("exchangeCode").toString();
                String branchCode = data.get("branchCode").toString();
                String bankName = data.get("bankName").toString();
                String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                duplicateData = cocPaidModelRepository.findByTransactionNoEqualsIgnoreCaseAndAmountAndExchangeCode(transactionNo, amount, exchangeCode);
                Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, "", fileInfoModel, user, currentDateTime, duplicateMessage, duplicateData, transactionList);
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
                CocPaidModel cocPaidModel = new CocPaidModel();
                cocPaidModel = CommonService.createDataModel(cocPaidModel, data);
                cocPaidModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                cocPaidModel.setUploadDateTime(currentDateTime);
                cocPaidModel.setFileInfoModel(fileInfoModel);
                cocPaidModel.setUserModel(user);
                cocPaidModelList.add(cocPaidModel);
                totalAmount += amount;
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && cocPaidModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("cocPaidModelList", cocPaidModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
            resp.put("totalAmount", totalAmount);
            //return cocPaidModelList;
            return resp;
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
    }

    public Map<String, Object> getCsvData(CSVRecord csvRecord){
        String routingNo = CommonService.fixRoutingNo(csvRecord.get(8));
        Map<String, Object> routingMap = new HashMap<>();
        if(!routingNo.isEmpty())    routingMap = customQueryService.getRoutingDetailsByRoutingNo(routingNo);
        String branchName = (routingMap.containsKey("branch_name")) ? routingMap.get("branch_name").toString(): "";
        String branchCode = (routingMap.containsKey("abl_branch_code")) ? routingMap.get("abl_branch_code").toString(): "";
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", csvRecord.get(0));
        data.put("transactionNo", csvRecord.get(1));
        data.put("amount", csvRecord.get(4));
        data.put("enteredDate", csvRecord.get(3));
        data.put("paidDate", CommonService.convertStringToDate(csvRecord.get(11)));
        data.put("remitterName", csvRecord.get(5));
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", csvRecord.get(7));
        data.put("routingNo", routingNo);
        data.put("beneficiaryMobile", csvRecord.get(10));
        data.put("bankName", "Agrani Bank");
        data.put("bankCode", "11");
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("trMode", csvRecord.get(12));
        return data;
    }

    public List<CocPaidModel> getProcessedDataByFileId(int fileInfoModelId, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return cocPaidModelRepository.getProcessedDataByUploadDateAndFileId(fileInfoModelId, isVoucherGenerated, starDateTime, enDateTime);
    }

    @Transactional
    public void updateIsVoucherGenerated(int id, int isVoucherGenerated, LocalDateTime reportDate){
        cocPaidModelRepository.updateIsVoucherGenerated(id, isVoucherGenerated, reportDate);
    }
    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        cocPaidModelRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }

    public List<CocPaidModel> findAllCocPaidModelHavingFileInfoId(int id){
        return cocPaidModelRepository.findAllCocPaidModelHavingFileInfoId(id);
    }

    public List<CocPaidModel> findCocPaidModelByFileInfoModelIdAndIsVoucherGenerated(int fileInfoModelId){
        return cocPaidModelRepository.findCocPaidModelByFileInfoModelIdAndIsVoucherGenerated(fileInfoModelId, 1);
    }
}
