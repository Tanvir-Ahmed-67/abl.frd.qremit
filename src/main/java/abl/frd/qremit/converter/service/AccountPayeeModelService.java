package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.model.AccountPayeeModel;
import abl.frd.qremit.converter.helper.AccountPayeeModelServiceHelper;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountPayeeModelService {
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<AccountPayeeModel> accountPayeeModes = accountPayeeModelRepository.findAllAccountPayeeModelHavingFileInfoId(CommonService.convertStringToInt(fileId));
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
                    existingEntity.setDownloadDateTime(CommonService.getCurrentDateTime());
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

    @Transactional
    public void updateIsVoucherGenerated(int id, int isVoucherGenerated, LocalDateTime reportDate){
        accountPayeeModelRepository.updateIsVoucherGenerated(id, isVoucherGenerated, reportDate);
    }
    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        accountPayeeModelRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }

    public List<AccountPayeeModel> findAllAccountPayeeModelByFileInfoId(int id){
        return accountPayeeModelRepository.findAllAccountPayeeModelHavingFileInfoId(id);
    }
    @Transactional
    public void updateTempStatusById(int id, int tempStatus){
        accountPayeeModelRepository.updateTempStatusById(id, tempStatus);
    }
    @Transactional
    public void updateTempStatusBulk(List<Integer> ids, int tempStatus){
        accountPayeeModelRepository.updateTempStatusBulk(ids, tempStatus);
    }

    public List<AccountPayeeModel> getDataByTransactionNoOrBenificiaryAccount(String type, String searchValue){
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        switch (type) {
            case "1":
                accountPayeeModelList = accountPayeeModelRepository.findAccountPayeeModelByTransactionNo(searchValue);
                break;
            case "2":    
                accountPayeeModelList = accountPayeeModelRepository.findAccountPayeeModelByBeneficiaryAccount(searchValue);
                break;
        }
        return accountPayeeModelList;
    }
}
