package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.BeftnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface BeftnModelRepository extends JpaRepository<BeftnModel, Integer> {
    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id =?1")
    List<BeftnModel> findAllBeftnModelHavingFileInfoId(int id);

    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id =?1")
    List<BeftnModel> findAllBeftnModelHavingFileInfoIdForIncentive(int id);
    @Query("SELECT n FROM BeftnModel n")
    List<BeftnModel> findAllBeftnModel();

    @Query("SELECT n FROM BeftnModel n")
    List<BeftnModel> findAllBeftnModelForIncentive();
    Integer countByIsProcessedMain(int isProcessed);
    @Query("SELECT COUNT(n) FROM BeftnModel n WHERE n.isProcessedIncentive = :isProcessed AND n.incentive <> 0")
    Integer countByIsProcessedIncentive(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM BeftnModel n WHERE n.isProcessedMain= :isProcessed")
    List<BeftnModel> loadUnprocessedBeftnMainData(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM BeftnModel n WHERE n.isProcessedIncentive= :isProcessed AND n.incentive <> 0")
    List<BeftnModel> loadUnprocessedBeftnIncentiveData(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM BeftnModel n WHERE n.isProcessed= :isProcessed and n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<BeftnModel> getProcessedDataByUploadDate(@Param("isProcessed") int isProcessed, @Param("isVoucherGenerated") int isVoucherGenerated, 
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isProcessed= :isProcessed AND n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate and n.tempStatus=0")
    List<BeftnModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isProcessed") int isProcessed, 
        @Param("isVoucherGenerated") int isVoucherGenerated, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE BeftnModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate WHERE n.id=:id")
    int updateIsVoucherGenerated(@Param("id") int id, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Transactional
    @Modifying
    @Query("UPDATE BeftnModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate, n.tempStatus = 1 WHERE n.id in :ids")
    int updateIsVoucherGeneratedBulk(@Param("ids") List<Integer> ids, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Transactional
    @Modifying
    @Query("UPDATE BeftnModel n SET n.tempStatus=:tempStatus WHERE n.id=:id")
    int updateTempStatusById(@Param("id") int id, @Param("tempStatus") int tempStatus);
    @Transactional
    @Modifying
    @Query("UPDATE BeftnModel n SET n.tempStatus=:tempStatus WHERE n.id in :ids")
    int updateTempStatusBulk(@Param("ids") List<Integer> ids, @Param("tempStatus") int tempStatus);
    List<BeftnModel> findBeftnModelByTransactionNo(String transactionNo);
    List<BeftnModel> findBeftnModelByBeneficiaryAccount(String beneficiaryAccount);
    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isDownloaded= :isDownloaded")
    List<BeftnModel> findBeftnModelByFileInfoModelIdAndIsDownloaded(@Param("fileInfoModelId") int fileInfoModelId, @Param("isDownloaded") int isDownloaded);
    @Query("SELECT n FROM BeftnModel n WHERE n.transactionNo = :transactionNo AND n.isDownloaded= :isDownloaded")
    List<BeftnModel> findBeftnModelByTransactionNoAndIsDownloaded(@Param("transactionNo") String transactionNo, @Param("isDownloaded") int isDownloaded);
    @Transactional
    @Modifying
    @Query("DELETE FROM BeftnModel n WHERE n.fileInfoModel.id = :fileInfoModelId")
    void deleteByFileInfoModelId(@Param("fileInfoModelId") int fileInfoModelId);
    @Transactional
    @Modifying
    @Query("UPDATE BeftnModel n SET n.incentive=:incentive, n.govtIncentive=:incentive,n.agraniIncentive=:incentive WHERE n.id in :ids")
    int updateNotProcessingIncentive(@Param("ids") List<Integer> ids, @Param("incentive") Double incentive);
    BeftnModel findByIdAndIsDownloaded(int id, int isDownloaded);
    @Query("SELECT n FROM BeftnModel n WHERE n.exchangeCode = :exchangeCode AND n.uploadDateTime BETWEEN :startDate AND :endDate")
    List<BeftnModel> findBeftnModelByExchangeCodeAndUploadDateTime(@Param("exchangeCode") String exchangeCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT sum(amount) as amount, count(n) as cnt FROM BeftnModel n where n.downloadDateTime BETWEEN :startDate AND :endDate and n.isProcessedMain=:isProcessed")
    List<Object[]> getDailyProcessedMainDataByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("isProcessed") int isProcessed);
    @Query("SELECT sum(incentive) as amount, count(n) as cnt FROM BeftnModel n where n.downloadDateTime BETWEEN :startDate AND :endDate and n.isProcessedIncentive = :isProcessed")
    List<Object[]> getDailyProcessedIncentiveDataByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("isProcessed") int isProcessed);
    List<BeftnModel> findByTxnModifiedIn(List<String> txnList);
}

