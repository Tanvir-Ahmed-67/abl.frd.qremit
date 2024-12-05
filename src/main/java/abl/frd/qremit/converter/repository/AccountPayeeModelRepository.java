package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.AccountPayeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isProcessed= :isProcessed AND n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate and n.tempStatus=0")
    List<AccountPayeeModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isProcessed") int isProcessed, 
        @Param("isVoucherGenerated") int isVoucherGenerated, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE AccountPayeeModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate WHERE n.id=:id")
    int updateIsVoucherGenerated(@Param("id") int id, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Transactional
    @Modifying
    @Query("UPDATE AccountPayeeModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate, n.tempStatus = 1 WHERE n.id in :ids")
    int updateIsVoucherGeneratedBulk(@Param("ids") List<Integer> ids, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Transactional
    @Modifying
    @Query("UPDATE AccountPayeeModel n SET n.tempStatus=:tempStatus WHERE n.id=:id")
    int updateTempStatusById(@Param("id") int id, @Param("tempStatus") int tempStatus);
    @Transactional
    @Modifying
    @Query("UPDATE AccountPayeeModel n SET n.tempStatus=:tempStatus WHERE n.id in :ids")
    int updateTempStatusBulk(@Param("ids") List<Integer> ids, @Param("tempStatus") int tempStatus);
}
