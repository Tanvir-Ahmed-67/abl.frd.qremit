package abl.frd.qremit.converter.repository;
import java.time.*;
import java.util.*;

import abl.frd.qremit.converter.model.ReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportModelRepository  extends JpaRepository<ReportModel, Integer>{
    Optional<ReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate = :reportDate")
    List<ReportModel> getReportModelByReportDate(@Param("reportDate") LocalDate reportDate);
}
