package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.AccountPayeeModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.CocModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.OnlineModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.CocModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountPayeeModelService {
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<AccountPayeeModel> accountPayeeModes = accountPayeeModelRepository.findAllAccountPayeeModelHavingFileInfoId(Integer.parseInt(fileId));
        ByteArrayInputStream in = AccountPayeeModelServiceHelper.AccountPayeeModelToCSV(accountPayeeModes);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<AccountPayeeModel> accountPayeeModes = accountPayeeModelRepository.findAllAccountPayeeModel();
        ByteArrayInputStream in = AccountPayeeModelServiceHelper.AccountPayeeModelToCSV(accountPayeeModes);
        return in;
    }
    public ByteArrayInputStream loadAndUpdateUnprocessedAccountPayeeData(int isProcessed) {
        List<AccountPayeeModel> unprocessedAccountPayeeModels = accountPayeeModelRepository.loadUnprocessedAccountPayeeData(isProcessed);
        List<AccountPayeeModel> processedAndUpdatedAccountPayeeModels = updateAndReturn(unprocessedAccountPayeeModels, 1);
        ByteArrayInputStream in = AccountPayeeModelServiceHelper.AccountPayeeModelToCSV(processedAndUpdatedAccountPayeeModels);
        return in;
    }
    public List<AccountPayeeModel> updateAndReturn(List<AccountPayeeModel> entitiesToUpdate, int processed) {
        // Retrieve the entities you want to update
        List<AccountPayeeModel> existingEntities = entitiesToUpdate;
        // Update the entities
        for (AccountPayeeModel existingEntity : existingEntities) {
            for (AccountPayeeModel updatedEntity : entitiesToUpdate) {
                if (existingEntity.getId() == (updatedEntity.getId())) {
                    existingEntity.setIsProcessed(processed);
                    existingEntity.setIsDownloaded(processed);
                    existingEntity.setDownloadDateTime(LocalDateTime.now());
                    existingEntity.setDownloadUserId(myUserDetailsService.getCurrentUser());
                    // Update other properties as needed
                    break;
                }
            }
        }
        // Save the modified entities
        List<AccountPayeeModel> updatedEntities = accountPayeeModelRepository.saveAll(existingEntities);
        return updatedEntities;
    }
    public int countRemainingAccountPayeeData(){
        return accountPayeeModelRepository.countByIsProcessed(0);
    }

    public List<AccountPayeeModel> getTemopraryReportData(int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return accountPayeeModelRepository.getProcessedDataByUploadDate(isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }

    public List<AccountPayeeModel> getProcessedDataByFileId(int fileInfoModelId,int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return accountPayeeModelRepository.getProcessedDataByUploadDateAndFileId(fileInfoModelId, isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }


}
