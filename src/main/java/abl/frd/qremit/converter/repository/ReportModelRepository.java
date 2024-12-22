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
    List<ReportModel> findReportModelByTransactionNo(String transactionNo);
    List<ReportModel> findReportModelByBeneficiaryAccount(String beneficiaryAccount);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='3'")
    Object[] getAllBeftnSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type IN ('2', '4')")
    Object[] getAllOtherSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='1'")
    Object[] getAllOnlineSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=1")
    Object[] getAllApiSummaryForMo(@Param("reportDate")LocalDate reportDate);
}
