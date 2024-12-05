package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.CocPaidModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CocPaidModelRepository extends JpaRepository<CocPaidModel, Integer> {
    Optional<CocPaidModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
    @Query("SELECT n FROM CocPaidModel n WHERE n.fileInfoModel.id = :fileInfoModelId AND n.isVoucherGenerated= :isVoucherGenerated and n.uploadDateTime BETWEEN :startDate AND :endDate and n.tempStatus=0")
    List<CocPaidModel> getProcessedDataByUploadDateAndFileId(@Param("fileInfoModelId") int fileInfoModelId, @Param("isVoucherGenerated") int isVoucherGenerated, 
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE CocPaidModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate WHERE n.id=:id")
    int updateIsVoucherGenerated(@Param("id") int id, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Transactional
    @Modifying
    @Query("UPDATE CocPaidModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate, n.tempStatus = 1 WHERE n.id in :ids")
    int updateIsVoucherGeneratedBulk(@Param("ids") List<Integer> ids, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
    @Query("SELECT n FROM CocPaidModel n WHERE n.fileInfoModel.id=:id")
    List<CocPaidModel> findAllCocPaidModelHavingFileInfoId(int id);
}
