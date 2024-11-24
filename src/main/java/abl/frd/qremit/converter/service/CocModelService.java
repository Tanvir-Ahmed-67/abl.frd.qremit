package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.CocModel;
import abl.frd.qremit.converter.helper.CocModelServiceHelper;
import abl.frd.qremit.converter.repository.CocModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class CocModelService {
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<CocModel> cocModels = cocModelRepository.findAllCocModelHavingFileInfoId(CommonService.convertStringToInt(fileId));
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<CocModel> cocModels = cocModelRepository.findAllCocModel();
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedCocData(int isProcessed, int isDownloaded) {
        List<CocModel> unprocessedCocModels = cocModelRepository.loadUnprocessedCocData(isProcessed, isDownloaded);
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
                    existingEntity.setDownloadDateTime(CommonService.getCurrentDateTime());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<CocModel> updatedEntities = cocModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public int countRemainingCocData(){
        //return CocModelRepository.countByIsProcessed(0);
        return cocModelRepository.countByIsDownloaded(0);
    }
    public List<CocModel> findAllCocModelByFileInfoId(int id){
        return cocModelRepository.findAllCocModelHavingFileInfoId(id);
    }
}
