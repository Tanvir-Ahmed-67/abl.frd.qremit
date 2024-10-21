package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import abl.frd.qremit.converter.nafex.model.TemporaryReportModel;

@Repository
public interface TemporaryReportRepository extends JpaRepository<TemporaryReportModel, Integer>{
    Optional<TemporaryReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
    @Transactional
    @Modifying
    @Query(value= "TRUNCATE table temporary_report", nativeQuery = true)
    void truncateTemporaryReport();
}
