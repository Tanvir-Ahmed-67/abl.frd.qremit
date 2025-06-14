package abl.frd.qremit.converter.repository;

import abl.frd.qremit.converter.model.BeftnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

}
/*
n.beneficiaryName not like '%PHONE%' or n.beneficiaryName not like '%CENTER%' or n.beneficiaryName not like '%LTD%' or n.beneficiaryName not like '%BANK%' or n.beneficiaryName not like '%TELECOM%'\n" +
            "        or n.beneficiaryName not like '%TRADERS%' or n.beneficiaryName not like '%STORE%' or n.beneficiaryName not like '%CLOTH%' or n.beneficiaryName not like '%BROTHERS%' or n.beneficiaryName not like '%ENTERPRIZE%'\n" +
            "        or n.beneficiaryName not like '%ENTERPRI%' or n.beneficiaryName not like '%COSMETICS%' or n.beneficiaryName not like '%MOBILE%' or n.beneficiaryName not like '%TRAVELS%' or n.beneficiaryName not like '%TOURS%'\n" +
            "        or n.beneficiaryName not like '%NETWORK%' or n.beneficiaryName not like '%ASSETS%' or n.beneficiaryName not like '%SOLUTIONS%' or n.beneficiaryName not like '%FUND%' or n.beneficiaryName not like '%ELECTRON%'\n" +
            "        or n.beneficiaryName not like '%SECURITIES%' or n.beneficiaryName not like '%EQUIPMENT%' or n.beneficiaryName not like '%COMPENSATION%' or n.beneficiaryName not like '%DEATH%'\n" +
            "        or n.beneficiaryName not like '%GALLERY%' or n.beneficiaryName not like '%HOUSE%' or n.beneficiaryName not like '%M/S%' or n.beneficiaryName not like '%BANGLADESH%' or n.beneficiaryName not like '%BD%'\n" +
            "        or n.beneficiaryName not like '%LIMITED%' or n.beneficiaryName not like '%OVERSEAS%' or n.beneficiaryName not like '%DAIRY%' or n.beneficiaryName not like '%FARM%' or n.beneficiaryName not like '%COLLECTION%'\n" +
            "        or n.beneficiaryName not like '%BANGLADESH%' or n.beneficiaryName not like '%RICE%' or n.beneficiaryName not like '%AGENCY%' or n.beneficiaryName not like '%TEXTILE%'\n" +
            "        or n.beneficiaryName not like '%VARAITY%' or n.beneficiaryName not like '%MEDICAL%' or n.beneficiaryName not like '%HALL%' or n.beneficiaryName not like '%PHARMA%'\n" +
            "        or n.beneficiaryName not like '%OPTICAL%' or n.beneficiaryName not like '%FAIR%' or n.beneficiaryName not like '%PRIZE%' or n.beneficiaryName not like '%GENERAL%'\n" +
            "        or n.beneficiaryName not like '%HOSPITAL%' or n.beneficiaryName not like '%BITAN%' or n.beneficiaryName not like '%TRADING%'

 */

