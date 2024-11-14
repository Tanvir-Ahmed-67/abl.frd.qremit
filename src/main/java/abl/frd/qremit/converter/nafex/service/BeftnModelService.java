package abl.frd.qremit.converter.nafex.service;
import abl.frd.qremit.converter.nafex.helper.BeftnModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.BeftnModel;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

@Service
public class BeftnModelService {
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
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

}
