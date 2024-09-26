package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountPayeeModelRepository extends JpaRepository<AccountPayeeModel, Integer> {
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.fileInfoModel.id =?1")
    List<AccountPayeeModel> findAllAccountPayeeModelHavingFileInfoId(int id);
    @Query("SELECT n FROM AccountPayeeModel n")
    List<AccountPayeeModel> findAllAccountPayeeModel();
    Integer countByIsProcessed(int isProcessed);
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.isProcessed= :isProcessed")
    List<AccountPayeeModel> loadUnprocessedAccountPayeeData(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.isProcessed= :isProcessed and n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<AccountPayeeModel> getProcessedDataByUploadDate(@Param("isProcessed") int isProcessed, @Param("isVoucherGenerated") int isVoucherGenerated, 
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isProcessed= :isProcessed AND n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<AccountPayeeModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isProcessed") int isProcessed, 
        @Param("isVoucherGenerated") int isVoucherGenerated, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
}
