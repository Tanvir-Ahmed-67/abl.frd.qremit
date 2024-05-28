package abl.frd.qremit.converter.nafex.service;
import abl.frd.qremit.converter.nafex.helper.BeftnModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.BeftnModel;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BeftnModelService {
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelHavingFileInfoId(Long.parseLong(fileId));
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(beftnModels);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModel();
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(beftnModels);
        return in;
    }

    public ByteArrayInputStream loadIncentive(String fileId, String fileType) {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelHavingFileInfoIdForIncentive(Long.parseLong(fileId));
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnIncentiveModelsToExcel(beftnModels);
        return in;
    }
    public ByteArrayInputStream loadAllIncentive() {
        List<BeftnModel> beftnModels = beftnModelRepository.findAllBeftnModelForIncentive();
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnIncentiveModelsToExcel(beftnModels);
        return in;
    }

    public ByteArrayInputStream loadAndUpdateUnprocessedBeftnMainData(String isProcessed) {
        List<BeftnModel> unprocessedBeftnModels = beftnModelRepository.loadUnprocessedBeftnMainData(isProcessed);
        List<BeftnModel> processedAndUpdatedBeftnModels = updateAndReturnMainData(unprocessedBeftnModels, "1");
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(processedAndUpdatedBeftnModels);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedBeftnIncentiveData(String isProcessed) {
        List<BeftnModel> unprocessedBeftnModels = beftnModelRepository.loadUnprocessedBeftnIncentiveData(isProcessed);
        List<BeftnModel> processedAndUpdatedBeftnModels = updateAndReturnIncentiveData(unprocessedBeftnModels, "1");
        ByteArrayInputStream in = BeftnModelServiceHelper.BeftnMainModelsToExcel(processedAndUpdatedBeftnModels);
        return in;
    }
    public List<BeftnModel> updateAndReturnMainData(List<BeftnModel> entitiesToUpdate, String processed) {
        // Retrieve the entities you want to update
        List<BeftnModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (BeftnModel existingEntity : existingEntities) {
            for (BeftnModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessedMain(processed);
                    existingEntity.setDownloadDateTime(LocalDateTime.now());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<BeftnModel> updatedEntities = beftnModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public List<BeftnModel> updateAndReturnIncentiveData(List<BeftnModel> entitiesToUpdate, String processed) {
        // Retrieve the entities you want to update
        List<BeftnModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (BeftnModel existingEntity : existingEntities) {
            for (BeftnModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessedIncentive(processed);
                    existingEntity.setDownloadDateTime(LocalDateTime.now());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<BeftnModel> updatedEntities = beftnModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }


}
