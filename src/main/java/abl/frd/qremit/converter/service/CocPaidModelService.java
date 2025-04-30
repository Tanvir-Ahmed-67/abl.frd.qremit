package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.CocPaidModel;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.CocPaidModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
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
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String tbl){
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

            Map<String, Object> cocPaidData = csvToCocPaidModels(file.getInputStream(), user, fileInfoModel, currentDateTime, tbl);
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
    public Map<String, Object> csvToCocPaidModels(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String tbl) {
        Map<String, Object> resp = new HashMap<>();
        Optional<CocPaidModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
           
            int i = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            //int duplicateCount = 0;
            Double totalAmount = 0.0;
            for (CSVRecord csvRecord : csvRecords) {
                i++;
                Map<String, Object> data = getCsvData(csvRecord);
                String transactionNo = data.get("transactionNo").toString();
                String amountStr = data.get("amount").toString();
                Double amount = CommonService.convertStringToDouble(amountStr);
                String exchangeCode = data.get("exchangeCode").toString();
                Map<String, Object> nrtaMap = CommonService.getExchangeCodeVsNrtaCodeMap(exchangeCode, exchangeHouseModelList);
                String nrtaCode = nrtaMap.get(exchangeCode).toString();
                data.put("nrtaCode", nrtaCode);
                double govtIncentive = CommonService.calculateGovtIncentivePercentage(amount);
                double agraniIncentive = CommonService.calculateAgraniIncentivePercentage(amount);
                String incentive = CommonService.convertDoubleToString(CommonService.calculateIncentive(govtIncentive, agraniIncentive));
                data.put("govtIncentive", CommonService.convertDoubleToString(govtIncentive));
                data.put("agraniIncentive", CommonService.convertDoubleToString(agraniIncentive));
                data.put("incentive", incentive);
                data.put("fileInfoModel", fileInfoModel);
                data.put("userModel", user);
                fileExchangeCode = nrtaCode;   
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amountStr, exchangeCode, uniqueKeys);
                totalAmount += amount;
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, CocPaidModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
            List<CocPaidModel> cocPaidModelList  = (List<CocPaidModel>) modelResp.get("modelList");
            for(CocPaidModel cocPaidModel: cocPaidModelList){
                cocPaidModel.setFileInfoModel(fileInfoModel);
                cocPaidModel.setUserModel(user);
            }
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
            if(errorDataModelList.isEmpty() && cocPaidModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("cocPaidModelList", cocPaidModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
            resp.put("totalAmount", totalAmount);
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
        LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(3));
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", csvRecord.get(0));
        data.put("transactionNo", csvRecord.get(1));
        data.put("amount", csvRecord.get(4));
        data.put("enteredDate", enteredDate.toLocalDate().toString());
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
