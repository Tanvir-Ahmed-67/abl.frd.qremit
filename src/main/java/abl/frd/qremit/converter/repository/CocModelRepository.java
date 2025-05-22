package abl.frd.qremit.converter.repository;

import abl.frd.qremit.converter.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CocModelRepository extends JpaRepository<CocModel, Integer> {
    @Query("SELECT n FROM CocModel n WHERE n.fileInfoModel.id =?1")
    List<CocModel> findAllCocModelHavingFileInfoId(int id);
    @Query("SELECT n FROM CocModel n")
    List<CocModel> findAllCocModel();
    Integer countByIsProcessed(int isProcessed);
    Integer countByIsDownloaded(int isDownloaded);

    @Query("SELECT n FROM CocModel n WHERE n.isProcessed= :isProcessed and n.isDownloaded= :isDownloaded")
    List<CocModel> loadUnprocessedCocData(@Param("isProcessed") int isProcessed, @Param("isDownloaded") int isDownloaded);
    List<CocModel> findCocModelByTransactionNo(String transactionNo);
    List<CocModel> findCocModelByBeneficiaryAccount(String beneficiaryAccount);
    @Query("SELECT n FROM CocModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isDownloaded= :isDownloaded")
    List<CocModel> findCocModelByFileInfoModelIdAndIsDownloaded(@Param("fileInfoModelId") int fileInfoModelId, @Param("isDownloaded") int isDownloaded);
    @Query("SELECT n FROM CocModel n WHERE n.transactionNo = :transactionNo AND n.isDownloaded= :isDownloaded")
    List<CocModel> findCocModelByTransactionNoAndIsDownloaded(@Param("transactionNo") String transactionNo, @Param("isDownloaded") int isDownloaded);
    @Transactional
    @Modifying
    @Query("DELETE FROM CocModel n WHERE n.fileInfoModel.id = :fileInfoModelId")
    void deleteByFileInfoModelId(@Param("fileInfoModelId") int fileInfoModelId);
    CocModel findByIdAndIsDownloaded(int id, int isDownloaded);
    @Query("SELECT n FROM CocModel n WHERE n.exchangeCode = :exchangeCode AND n.uploadDateTime BETWEEN :startDate AND :endDate")
    List<CocModel> findCocModelByExchangeCodeAndUploadDateTime(@Param("exchangeCode") String exchangeCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
