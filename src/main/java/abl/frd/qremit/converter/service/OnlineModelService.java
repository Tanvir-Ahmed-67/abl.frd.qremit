package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.helper.OnlineModelServiceHelper;
import abl.frd.qremit.converter.model.OnlineModel;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.ReportModelRepository;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.transaction.Transactional;
@Service
public class OnlineModelService {
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    ReportModelRepository reportModelRepository;

    public ByteArrayInputStream load(String fileId, String fileType) {
        List<OnlineModel> onlineModes = onlineModelRepository.findAllOnlineModelHavingFileInfoId(CommonService.convertStringToInt(fileId));
        ByteArrayInputStream in = OnlineModelServiceHelper.OnlineModelToCSV(onlineModes);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<OnlineModel> onlineModes = onlineModelRepository.findAllOnlineModel();
        ByteArrayInputStream in = OnlineModelServiceHelper.OnlineModelToCSV(onlineModes);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedOnlineData(int isProcessed) {
        List<OnlineModel> unprocessedOnlineModels = onlineModelRepository.loadUnprocessedOnlineData(isProcessed);
        List<OnlineModel> processedAndUpdatedOnlineModels = updateAndReturn(unprocessedOnlineModels, 1);
        ByteArrayInputStream in = OnlineModelServiceHelper.OnlineModelToCSV(processedAndUpdatedOnlineModels);
        return in;
    }

    public ByteArrayInputStream loadProcessedOnlineData(int isProcessed) {
        List<OnlineModel> onlineModels = onlineModelRepository.loadProcessedOnlineData(isProcessed);
        ByteArrayInputStream in = OnlineModelServiceHelper.OnlineModelToCSV(onlineModels);
        return in;
    }

    public int countProcessedOnlineData(int isProcessed){
        return onlineModelRepository.countByIsProcessed(isProcessed);
    }
    public int countUnProcessedOnlineData(int isProcessed){
        return onlineModelRepository.countByIsProcessed(isProcessed);
    }
    public List<OnlineModel> updateAndReturn(List<OnlineModel> entitiesToUpdate, int processed) {
        // Retrieve the entities you want to update
        List<OnlineModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (OnlineModel existingEntity : existingEntities) {
            for (OnlineModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessed(processed);
                    existingEntity.setIsDownloaded(processed);
                    existingEntity.setDownloadDateTime(CommonService.getCurrentDateTime());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<OnlineModel> updatedEntities = onlineModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public int countRemainingOnlineData(){
        return onlineModelRepository.countByIsProcessed(0);
    }

    public List<OnlineModel> getTemopraryReportData(int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return onlineModelRepository.getProcessedDataByUploadDate(isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }

    public List<OnlineModel> getProcessedDataByFileId(int fileInfoModelId,int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return onlineModelRepository.getProcessedDataByUploadDateAndFileId(fileInfoModelId,isProcessed,isVoucherGenerated,starDateTime,enDateTime);
    }
    @Transactional
    public void updateIsVoucherGenerated(int id, int isVoucherGenerated, LocalDateTime reportDate){
        onlineModelRepository.updateIsVoucherGenerated(id, isVoucherGenerated, reportDate);
    }
    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        onlineModelRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }
    public List<OnlineModel> findAllOnlineModelByFileInfoId(int id){
        return onlineModelRepository.findAllOnlineModelHavingFileInfoId(id);
    }
    @Transactional
    public void updateTempStatusById(int id, int tempStatus){
        onlineModelRepository.updateTempStatusById(id, tempStatus);
    }
    @Transactional
    public void updateTempStatusBulk(List<Integer> ids, int tempStatus){
        onlineModelRepository.updateTempStatusBulk(ids, tempStatus);
    }

    public List<OnlineModel> getDataByTransactionNoOrBenificiaryAccount(String type, String searchValue){
        List<OnlineModel> onlineModelList = new ArrayList<>();
        switch (type) {
            case "1":
                onlineModelList = onlineModelRepository.findOnlineModelByTransactionNo(searchValue);
                break;
            case "2":    
                onlineModelList =onlineModelRepository.findOnlineModelByBeneficiaryAccount(searchValue);
                break;
        }
        return onlineModelList;
    }

    public List<OnlineModel> getOnlineModelByTransactionNoAndIsDownloaded(String transactionNo, int isDownloaded){
        return onlineModelRepository.findOnlineModelByTransactionNoAndIsDownloaded(transactionNo, isDownloaded);
    }

    public Optional<OnlineModel> getOnlineModelById(int id){
        return onlineModelRepository.findById(id);
    }
    public OnlineModel findOnlineModelByIdAndIsDownloaded(int id, int isDownloaded){
        return onlineModelRepository.findByIdAndIsDownloaded(id, isDownloaded);
    }

    public List<OnlineModel> findOnlineModelByFileInfoModelIdAndIsDownloaded(int fileInfoModelId, int isSettlement){
        if(isSettlement == 1){
            return onlineModelRepository.findOnlineModelByApiAndFileInfoModelId(fileInfoModelId, 1);
        }else return onlineModelRepository.findOnlineModelByFileInfoModelIdAndIsDownloaded(fileInfoModelId, 1);
    }

    public List<OnlineModel> findOnlineModelByExchangeCodeAndUploadDateTime(String exchangeCode, LocalDateTime startDate, LocalDateTime endDate){
        return onlineModelRepository.findOnlineModelByExchangeCodeAndUploadDateTime(exchangeCode, startDate, endDate);
    }
    public List<Object[]> getDailyProcessedDataByDate(LocalDateTime startDate, LocalDateTime endDate, int isProcessed){
        return onlineModelRepository.getDailyProcessedDataByDate(startDate, endDate, isProcessed);
    }

    public Map<String, Object> uploadQremitIncentive(MultipartFile file, int userId, String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            //System.out.println(csvRecords);
            int cnt = 0;
            for (CSVRecord csvRecord : csvRecords) {
                String txnStr = csvRecord.get(0).trim();
                String transactionNo = txnStr.substring(0, txnStr.length() - 7);
                exchangeCode = txnStr.substring(txnStr.length() - 7);
                //Double amount = CommonService.convertStringToDouble(csvRecord.get(3).trim());
                String amountStr = csvRecord.get(3).trim();
                Double amount = CommonService.convertStringToDouble(amountStr);
                Double govtIncentive = CommonService.calculateGovtIncentivePercentage(amount);
                Double agraniIncentive = CommonService.calculateAgraniIncentivePercentage(amount);
                Double incentive = govtIncentive + agraniIncentive;
                int affected = updateQremitIncentive(transactionNo, exchangeCode, amount, govtIncentive, agraniIncentive, incentive);
                cnt += affected;
            }
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", file.getOriginalFilename());
            fileInfo.put("totalCount", cnt);
            resp = CommonService.getResp(0, "", null);
            resp.put("fileInfo", fileInfo);
        }catch(Exception e){
            String message = "fail to store csv data: " + e.getMessage();
            resp = CommonService.getResp(1, message, null);
        }
        return resp;
    }
    @Transactional
    public int updateQremitIncentive(String transactionNo, String exchangeCode, Double amount, Double govtIncentive, Double agraniIncentive, Double incentive){
        int affected = onlineModelRepository.updateQremitIncentive(transactionNo, exchangeCode, amount, govtIncentive, agraniIncentive, incentive);
        if(affected > 0){
            affected = reportModelRepository.updateQremitIncentive(transactionNo, exchangeCode, amount, govtIncentive, agraniIncentive, incentive);
        }
        return affected;
    }
}
