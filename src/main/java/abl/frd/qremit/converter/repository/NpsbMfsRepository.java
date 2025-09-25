package abl.frd.qremit.converter.repository;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import abl.frd.qremit.converter.model.NpsbMfsModel;

@Repository
public interface NpsbMfsRepository extends JpaRepository<NpsbMfsModel, Integer> {
    List<NpsbMfsModel> findNpsbMfsModelByTransactionNo(String transactionNo);
    List<NpsbMfsModel> findNpsbMfsModelByBeneficiaryAccount(String beneficiaryAccount);
    @Query("SELECT n FROM NpsbMfsModel n WHERE n.transactionNo = :transactionNo AND n.isDownloaded= :isDownloaded")
    List<NpsbMfsModel> findNpsbMfsModelByTransactionNoAndIsDownloaded(@Param("transactionNo") String transactionNo, @Param("isDownloaded") int isDownloaded);
    @Query("SELECT n FROM NpsbMfsModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isProcessed= :isProcessed AND n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate and n.tempStatus=0")
    List<NpsbMfsModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isProcessed") int isProcessed, 
        @Param("isVoucherGenerated") int isVoucherGenerated, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE NpsbMfsModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate, n.tempStatus = 1 WHERE n.id in :ids")
    int updateIsVoucherGeneratedBulk(@Param("ids") List<Integer> ids, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Query("SELECT n FROM NpsbMfsModel n WHERE n.isProcessed= :isProcessed and n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate")
    List<NpsbMfsModel> getProcessedDataByUploadDate(@Param("isProcessed") int isProcessed, @Param("isVoucherGenerated") int isVoucherGenerated, 
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE NpsbMfsModel n SET n.tempStatus=:tempStatus WHERE n.id in :ids")
    int updateTempStatusBulk(@Param("ids") List<Integer> ids, @Param("tempStatus") int tempStatus);
}
