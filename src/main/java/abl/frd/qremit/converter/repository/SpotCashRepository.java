package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import abl.frd.qremit.converter.model.SpotCashModel;
@Repository
public interface SpotCashRepository extends JpaRepository<SpotCashModel, Integer>{
    @Query("SELECT n.exchangeCodeSc, sum(n.amount) as totalAmount, count(n) as cnt FROM SpotCashModel n WHERE n.uploadDateTime BETWEEN :startDate AND :endDate group by n.exchangeCode order by n.exchangeCodeSc")
    List<Object[]> getExchangeWiseDailyDataByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
