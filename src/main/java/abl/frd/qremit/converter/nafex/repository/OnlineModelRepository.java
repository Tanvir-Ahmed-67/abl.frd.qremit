package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.NafexEhMstModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OnlineModelRepository extends JpaRepository<OnlineModel, Integer> {
    @Query("SELECT n FROM OnlineModel n WHERE n.fileInfoModel.id =?1")
    List<OnlineModel> findAllOnlineModelHavingFileInfoId(int id);
    @Query("SELECT n FROM OnlineModel n")
    List<OnlineModel> findAllOnlineModel();
    @Query("SELECT n FROM OnlineModel n WHERE n.isProcessed= :isProcessed")
    List<OnlineModel> loadUnprocessedOnlineData(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM OnlineModel n WHERE n.isProcessed= :isProcessed")
    List<OnlineModel> loadProcessedOnlineData(@Param("isProcessed") int isProcessed);
    Integer countByIsProcessed(int isProcessed);
    @Query("SELECT n FROM OnlineModel n WHERE n.isProcessed= :isProcessed and n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<OnlineModel> getProcessedDataByUploadDate(@Param("isProcessed") int isProcessed, @Param("isVoucherGenerated") int isVoucherGenerated, 
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT n FROM OnlineModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isProcessed= :isProcessed AND n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<OnlineModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isProcessed") int isProcessed, 
        @Param("isVoucherGenerated") int isVoucherGenerated, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE OnlineModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate WHERE n.id=:id")
    int updateIsVoucherGenerated(@Param("id") int id, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
}
