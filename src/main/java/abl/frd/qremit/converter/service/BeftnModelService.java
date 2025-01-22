package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.helper.BeftnModelServiceHelper;
import abl.frd.qremit.converter.model.BeftnModel;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CustomQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
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

}
