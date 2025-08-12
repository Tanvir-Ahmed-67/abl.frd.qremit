package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.helper.BeftnModelServiceHelper;
import abl.frd.qremit.converter.model.BeftnModel;
import abl.frd.qremit.converter.model.BeftnReturnModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.BeftnReturnRepository;
import abl.frd.qremit.converter.repository.CustomQueryRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.transaction.Transactional;
@SuppressWarnings("unchecked")
@Service
public class BeftnModelService {
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    CustomQueryRepository customQueryRepository;
    @Autowired
    BeftnReturnRepository beftnReturnRepository;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelHavingFileInfoId(CommonService.convertStringToInt(fileId));
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(beftnModels);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModel();
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(beftnModels);
        return in;
    }

    public ByteArrayInputStream loadIncentive(String fileId, String fileType) {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelHavingFileInfoIdForIncentive(CommonService.convertStringToInt(fileId));
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnIncentiveModelsToExcel(beftnModels);
        return in;
    }
    public ByteArrayInputStream loadAllIncentive() {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelForIncentive();
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnIncentiveModelsToExcel(beftnModels);
        return in;
    }

    public ByteArrayInputStream loadAndUpdateUnprocessedBeftnMainData(int isProcessed) {
        List<BeftnModel> unprocessedBeftnModels = beftnModelRepository.loadUnprocessedBeftnMainData(isProcessed);
        List<BeftnModel> processedAndUpdatedBeftnModels = updateAndReturnMainData(unprocessedBeftnModels, 1);
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(processedAndUpdatedBeftnModels);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedBeftnIncentiveData(int isProcessed) {
        List<BeftnModel> unprocessedBeftnModels = beftnModelRepository.loadUnprocessedBeftnIncentiveData(isProcessed);
        List<BeftnModel> processedAndUpdatedBeftnModels = updateAndReturnIncentiveData(unprocessedBeftnModels, 1);
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnIncentiveModelsToExcel(processedAndUpdatedBeftnModels);
        return in;
    }
    public List<BeftnModel> updateAndReturnMainData(List<BeftnModel> entitiesToUpdate, int processed) {
        // Retrieve the entities you want to update
        List<BeftnModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (BeftnModel existingEntity : existingEntities) {
            for (BeftnModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessedMain(processed);
                    existingEntity.setDownloadDateTime(CommonService.getCurrentDateTime());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    if(existingEntity.getIsProcessedMain() == 1 && existingEntity.getIncentive() == 0){
                        existingEntity.setIsDownloaded(1);
                        existingEntity.setIsProcessed(1);
                    }
                    else if(existingEntity.getIsProcessedMain() == 1 && existingEntity.getIsProcessedIncentive() == 1){
                        existingEntity.setIsDownloaded(1);
                        existingEntity.setIsProcessed(1);
                    }
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<BeftnModel> updatedEntities = beftnModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public List<BeftnModel> updateAndReturnIncentiveData(List<BeftnModel> entitiesToUpdate, int processed) {
        // Retrieve the entities you want to update
        List<BeftnModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (BeftnModel existingEntity : existingEntities) {
            for (BeftnModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessedIncentive(processed);
                    existingEntity.setDownloadDateTime(CommonService.getCurrentDateTime());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    if(existingEntity.getIsProcessedMain() == 1 && existingEntity.getIsProcessedIncentive() == 1){
                        existingEntity.setIsDownloaded(1);
                        existingEntity.setIsProcessed(1);
                    }
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<BeftnModel> updatedEntities = beftnModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public int countRemainingBeftnDataMain(){
        return beftnModelRepository.countByIsProcessedMain(0);
    }
    public int countRemainingBeftnDataIncentive(){
        return beftnModelRepository.countByIsProcessedIncentive(0);
    }

    public List<BeftnModel> getTemopraryReportData(int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return beftnModelRepository.getProcessedDataByUploadDate(isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }

    public List<BeftnModel> getProcessedDataByFileId(int fileInfoModelId,int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return beftnModelRepository.getProcessedDataByUploadDateAndFileId(fileInfoModelId, isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }

    @Transactional
    public void updateIsVoucherGenerated(int id, int isVoucherGenerated, LocalDateTime reportDate){
        beftnModelRepository.updateIsVoucherGenerated(id, isVoucherGenerated, reportDate);
    }
    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        beftnModelRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }
    public List<BeftnModel> findAllBeftnModelByFileInfoId(int id){
        return beftnModelRepository.findAllBeftnModelHavingFileInfoId(id);
    }
    
    @Transactional
    public void updateTempStatusById(int id, int tempStatus){
        beftnModelRepository.updateTempStatusById(id, tempStatus);
    }
    @Transactional
    public void updateTempStatusBulk(List<Integer> ids, int tempStatus){
        beftnModelRepository.updateTempStatusBulk(ids, tempStatus);
    }

    public List<BeftnModel> getDataByTransactionNoOrBenificiaryAccount(String type, String searchValue){
        List<BeftnModel> beftnModelList = new ArrayList<>();
        switch (type) {
            case "1":
                beftnModelList = beftnModelRepository.findBeftnModelByTransactionNo(searchValue);
                break;
            case "2":
                beftnModelList = beftnModelRepository.findBeftnModelByBeneficiaryAccount(searchValue);
                break;
        }
        return beftnModelList;
    }

    public List<BeftnModel> getBeftnModelByTransactionNoAndIsDownloaded(String transactionNo, int isDownloaded){
        return beftnModelRepository.findBeftnModelByTransactionNoAndIsDownloaded(transactionNo, isDownloaded);
    }

    public BeftnModel findBeftnModelByIdAndIsDownloaded(int id, int isDownloaded){
        return beftnModelRepository.findByIdAndIsDownloaded(id, isDownloaded);
    }

    public List<BeftnModel> findBeftnModelByFileInfoModelIdAndIsDownloaded(int fileInfoModelId){
        return beftnModelRepository.findBeftnModelByFileInfoModelIdAndIsDownloaded(fileInfoModelId, 1);
    }

    public Map<String, Object> calculateNotProcessingBeftnIncentive(){
        Map<String, Object> resp = new HashMap<>();
        String[] keywords = CommonService.beftnIncentiveNotProcessingKeywords();
        resp = customQueryRepository.getBeftnIncentiveNotProcessing(keywords);
        if((Integer) resp.get("err") == 1)  return resp;
        List<Integer> idList = new ArrayList<>();
        List<Map<String, Object>> dataList = (List<Map<String, Object>>)  resp.get("data");
        for(Map<String, Object> data: dataList){
            int id = CommonService.convertStringToInt(data.get("id").toString());
            idList.add(id);
        }
        return updateNotProcessingIncentive(idList);
    }

    

    @Transactional
    public Map<String,Object> updateNotProcessingIncentive(List<Integer> idList){
        if(!idList.isEmpty()){
            int rowsUpdated  = beftnModelRepository.updateNotProcessingIncentive(idList, 0.0);
            if(rowsUpdated > 0) return CommonService.getResp(0, "Data updated successful", null);
            else return CommonService.getResp(1, "No data updated", null);
        }else return CommonService.getResp(1, "No data updated", null);
    }

    public List<BeftnModel> findBeftnModelByExchangeCodeAndUploadDateTime(String exchangeCode, LocalDateTime startDate, LocalDateTime endDate){
        return beftnModelRepository.findBeftnModelByExchangeCodeAndUploadDateTime(exchangeCode, startDate, endDate);
    }

    public List<Object[]> getDailyProcessedMainDataByDate(LocalDateTime startDate, LocalDateTime endDate, int isProcessed){
        return beftnModelRepository.getDailyProcessedMainDataByDate(startDate, endDate, isProcessed);
    }

    public List<Object[]> getDailyProcessedIncentiveDataByDate(LocalDateTime startDate, LocalDateTime endDate, int isProcessed){
        return beftnModelRepository.getDailyProcessedIncentiveDataByDate(startDate, endDate, isProcessed);
    }
    @Transactional
    public Map<String, Object> uploadBeftnReturn(MultipartFile file, int userId, String exCode){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        FileInfoModel fileInfoModel = new FileInfoModel();
        fileInfoModel.setExchangeCode(exCode);
        User user = userModelRepository.findByUserId(userId);
        fileInfoModel.setUserModel(user);
        fileInfoModel.setFileName(file.getOriginalFilename());
        fileInfoModel.setUploadDateTime(currentDateTime);
        fileInfoModelRepository.save(fileInfoModel);
        int fileInfoModelId = fileInfoModel.getId();        
        
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<Map<String, Object>> dataList = new ArrayList<>();
            List<String> txnLists = new ArrayList<>();
            for(CSVRecord csvRecord: csvRecords){
                String[] refStr = CommonService.parseString(csvRecord.get(37),"-");
                String txnNo = refStr[0].trim();
                String remType = refStr[1].trim();
                if(!remType.toLowerCase().contains("frd"))  continue;
                Map<String, Object> data = parseBeftnReturnData(csvRecord, txnNo, remType);
                txnLists.add(txnNo);
                dataList.add(data);
            }
            if(dataList.isEmpty()){
                fileInfoModelRepository.deleteById(fileInfoModelId);
                return CommonService.getResp(1, "No data found for processing", null);
            }
            List<BeftnModel> beftnModelList = beftnModelRepository.findByTxnModifiedIn(txnLists);
            Map<String, BeftnModel> beftnMap = new HashMap<>();
            for (BeftnModel beftn : beftnModelList) {
                beftnMap.put(beftn.getTxnModified(), beftn);
            }
            List<BeftnReturnModel> beftnReturnModelList = new ArrayList<>();
            int i= 0;
            for(Map<String, Object> data: dataList){
                String txnNo = data.get("txnModified").toString();
                BeftnModel matched = beftnMap.get(txnNo);
                int status = 0;
                String exchangeCode = "";
                String transactionNo = "";
                if(matched != null){
                    exchangeCode = matched.getExchangeCode();        
                    status = 1;
                    transactionNo = matched.getTransactionNo();
                }
                data.put("exchangeCode", exchangeCode);
                data.put("transactionNo", transactionNo);
                data.put("status", status);
                data.put("fileInfoModelId", fileInfoModelId);
                data.put("userId", user.getId());
                BeftnReturnModel beftnReturnModel = new BeftnReturnModel();
                beftnReturnModel = CommonService.createDataModel(beftnReturnModel, data);
                beftnReturnModelList.add(beftnReturnModel);
                i++;
            } 
            try{
                fileInfoModel.setTotalCount(CommonService.convertIntToString(i));
                beftnReturnRepository.saveAll(beftnReturnModelList);
                resp = CommonService.getResp(0,"Data Uploaded Successfully", null);
            }catch(Exception e){
                e.printStackTrace();
                resp =  CommonService.getResp(1, "Error Inserting data", null);
            }
        }catch(Exception e){
            e.printStackTrace();
            resp =  CommonService.getResp(1, "fail to store csv data: " + e.getMessage(), null);
        }
        if(resp.containsKey("err") && (Integer) resp.get("err") == 1)   fileInfoModelRepository.deleteById(fileInfoModelId);
        return resp;
    }

    public Map<String, Object> parseBeftnReturnData(CSVRecord csvRecord,String txnNo, String remType){
        Map<String, Object> data = new HashMap<>();
        data.put("txnModified", txnNo);
        data.put("remType", remType);
        String[] returnStr = CommonService.parseString(csvRecord.get(32),"-");
        String[] routing = CommonService.parseString(csvRecord.get(34),"-");
        String[] beneficiary = CommonService.parseString(csvRecord.get(36),"-");
        data.put("beneficiaryAccount", beneficiary[0].trim());
        data.put("beneficiaryName", beneficiary[1].trim());
        data.put("routingNo", routing[0].trim());
        data.put("returnCode", returnStr[0].trim());
        data.put("processedDate", CommonService.parseStringByDelimeter(csvRecord.get(8),":"));
        data.put("returnDate", CommonService.parseStringByDelimeter(csvRecord.get(42),":"));
        data.put("amount", CommonService.parseStringByDelimeter(csvRecord.get(31), ","));
        return data;
    }

    public Map<String, Object> getExchangeWiseBeftnReturnReport(Map<String, String> formData, int userId){
        Map<String, Object> resp = new HashMap<>();
        LocalDate starDate = CommonService.convertStringToLocalDate(formData.get("startDate"),"yyyy-MM-dd");
        LocalDate enDateTime = CommonService.convertStringToLocalDate(formData.get("endDate"), "yyyy-MM-dd");
        String exchangeCode = formData.get("exchangeCode");
        List<BeftnReturnModel> beftnReturnModelList = beftnReturnRepository.getBeftnReturnModelByExchangeCodeAndProcessedDate(exchangeCode, starDate, enDateTime);
        resp = proceessDataFromBeftnReturnModelList(beftnReturnModelList);
        return resp;
    }

    public Map<String, Object> getBeftnReturnReportByFileInfoModelId(int fileInfoModelId){
        List<BeftnReturnModel> beftnReturnModelList = beftnReturnRepository.getBeftnReturnModelByFileInfoModelId(fileInfoModelId);
        Map<String, Object> resp = proceessDataFromBeftnReturnModelList(beftnReturnModelList);
        return resp;
    }

    public Map<String, Object> proceessDataFromBeftnReturnModelList(List<BeftnReturnModel> beftnReturnModelList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        if(beftnReturnModelList.isEmpty())  return CommonService.getResp(1, "No data found", dataList);
        Map<String, Object> reasonResp = customQueryRepository.getBeftnReturnReason("");
        List<Map<String, Object>> reasonList = (List<Map<String, Object>>) reasonResp.get("data");
        int i = 1;
        for(BeftnReturnModel beftnReturnModel: beftnReturnModelList){
            Map<String, Object> data = new HashMap<>();
            String returnCode = beftnReturnModel.getReturnCode();
            String returnReason = "";
            for (Map<String, Object> reason : reasonList){
                String reasonCode = reason.get("return_code").toString();
                if(returnCode.equals(reasonCode)){
                    returnReason = reason.get("return_name").toString();
                    break;
                }
            }
            data.put("sl", i++);
            data.put("transactionNo", beftnReturnModel.getTransactionNo());
            data.put("amount", beftnReturnModel.getAmount());
            data.put("beneficiaryAccount", beftnReturnModel.getBeneficiaryAccount());
            data.put("beneficiaryName", beftnReturnModel.getBeneficiaryName());
            data.put("exchangeCode", beftnReturnModel.getExchangeCode());
            data.put("routingNo", beftnReturnModel.getRoutingNo());
            data.put("processedDate", beftnReturnModel.getProcessedDate());
            data.put("remType", beftnReturnModel.getRemType());
            data.put("returnCode", returnCode + " - " + returnReason);
            data.put("returnDate", beftnReturnModel.getReturnDate());
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }
}
