package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.AccountPayeeModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.CocModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.CocModel;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CocModelService {
    @Autowired
    CocModelRepository CocModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<CocModel> cocModels = CocModelRepository.findAllCocModelHavingFileInfoId(Integer.parseInt(fileId));
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<CocModel> cocModels = CocModelRepository.findAllCocModel();
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedCocData(int isProcessed, int isDownloaded) {
        List<CocModel> unprocessedCocModels = CocModelRepository.loadUnprocessedCocData(isProcessed, isDownloaded);
        List<CocModel> processedAndUpdatedCocModels = updateAndReturn(unprocessedCocModels, 0, 1);
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(processedAndUpdatedCocModels);
        return in;
    }
    public List<CocModel> updateAndReturn(List<CocModel> entitiesToUpdate, int processed, int downloaded) {
        // Retrieve the entities you want to update
        List<CocModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (CocModel existingEntity : existingEntities) {
            for (CocModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessed(processed);
                    existingEntity.setIsDownloaded(downloaded);
                    existingEntity.setDownloadDateTime(LocalDateTime.now());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<CocModel> updatedEntities = CocModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public int countRemainingCocData(){
        //return CocModelRepository.countByIsProcessed(0);
        return CocModelRepository.countByIsDownloaded(0);
    }

}
