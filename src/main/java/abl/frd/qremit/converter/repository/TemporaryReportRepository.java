package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.TemporaryReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemporaryReportRepository extends JpaRepository<TemporaryReportModel, Integer>{
    Optional<TemporaryReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
    @Transactional
    @Modifying
    @Query(value= "TRUNCATE table temporary_report", nativeQuery = true)
    void truncateTemporaryReport();
}
