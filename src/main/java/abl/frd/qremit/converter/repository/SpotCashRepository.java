package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import abl.frd.qremit.converter.model.SpotCashModel;
@Repository
public interface SpotCashRepository extends JpaRepository<SpotCashModel, Integer>{
    @Query("SELECT n.exchangeCodeSc, sum(n.amount) as totalAmount, count(n) as cnt FROM SpotCashModel n WHERE n.uploadDateTime BETWEEN :startDate AND :endDate group by n.exchangeCode order by n.exchangeCodeSc")
    List<Object[]> getExchangeWiseDailyDataByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT n FROM SpotCashModel n WHERE n.isProcessed= :isProcessed and n.isVoucherGenerated= :isVoucherGenerated and n.downloadDateTime BETWEEN :startDate AND :endDate and n.entryActive= :entryActive")
    List<SpotCashModel> getProcessedDataByUploadDate(@Param("isProcessed") int isProcessed, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("entryActive") int entryActive,
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Transactional
    @Modifying
    @Query("UPDATE SpotCashModel n SET n.isVoucherGenerated=:isVoucherGenerated, n.reportDate=:reportDate, n.tempStatus = 1, n.entryActive = 2 WHERE n.id in :ids")
    int updateIsVoucherGeneratedBulk(@Param("ids") List<Integer> ids, @Param("isVoucherGenerated") int isVoucherGenerated, @Param("reportDate") LocalDateTime reportdate);
}
