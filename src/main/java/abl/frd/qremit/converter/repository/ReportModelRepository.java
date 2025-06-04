package abl.frd.qremit.converter.repository;
import java.time.*;
import java.util.*;

import abl.frd.qremit.converter.model.ExchangeReportDTO;
import abl.frd.qremit.converter.model.ReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportModelRepository  extends JpaRepository<ReportModel, Integer>{
    Optional<ReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate = :reportDate")
    List<ReportModel> getReportModelByReportDate(@Param("reportDate") LocalDate reportDate);
    @Query("SELECT new abl.frd.qremit.converter.model.ExchangeReportDTO(n.exchangeCode, COUNT(n), SUM(n.amount)) " + "FROM ReportModel n WHERE n.reportDate = :reportDate " + "GROUP BY n.exchangeCode")
    List<ExchangeReportDTO> getGroupedReportByReportDate(@Param("reportDate") LocalDate reportDate);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate")
    List<ReportModel> getReportModelByReportDateRange(@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate AND n.isApi=0 AND n.type IN ('1','2','4')")
    List<ReportModel> getAllCocOnlineAndAccountPayeeDataDataByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate AND n.isApi=0 AND n.type IN ('2','4')")
    List<ReportModel> getAllCocAndAccountPayeeDataByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate AND n.isApi=0 AND n.type IN ('1')")
    List<ReportModel> getAllOnlineDataByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    List<ReportModel> findReportModelByTransactionNo(String transactionNo);
    List<ReportModel> findReportModelByBeneficiaryAccount(String beneficiaryAccount);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='3'")
    Object[] getAllBeftnSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Modifying
    @Query("UPDATE ReportModel n SET n.moNumber = :moNumber WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='3'")
    int updateMoNumberForAllBeftn(@Param("moNumber") String moNumber, @Param("reportDate") LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type IN ('2', '4')")
    Object[] getAllOtherSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Modifying
    @Query("UPDATE ReportModel n SET n.moNumber = :moNumber WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type IN ('2', '4')")
    int updateMoNumberForAllOther(@Param("moNumber") String moNumber, @Param("reportDate") LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='1'")
    Object[] getAllOnlineSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Modifying
    @Query("UPDATE ReportModel n SET n.moNumber = :moNumber WHERE n.reportDate = :reportDate AND n.isApi=0 AND n.type='1'")
    int updateMoNumberForAllOnline(@Param("moNumber") String moNumber, @Param("reportDate") LocalDate reportDate);
    @Query("SELECT COUNT(n), SUM(n.amount) FROM ReportModel n WHERE n.reportDate = :reportDate AND n.isApi=1")
    Object[] getAllApiSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Modifying
    @Query("UPDATE ReportModel n SET n.moNumber = :moNumber WHERE n.reportDate = :reportDate AND n.isApi=1")
    int updateMoNumberForAllApi(@Param("moNumber") String moNumber, @Param("reportDate") LocalDate reportDate);
    @Query("SELECT r.type, r.isApi, COUNT(r), SUM(r.amount) FROM ReportModel r WHERE r.reportDate = :reportDate GROUP BY r.type,r.isApi")
    Object[] getAllSummaryForMo(@Param("reportDate")LocalDate reportDate);
    @Query("SELECT n FROM ReportModel n WHERE n.exchangeCode = :exchangeCode AND n.reportDate BETWEEN :startDate AND :endDate ORDER BY n.reportDate")
    List<ReportModel> getReportModelByExchangeCodeAndReportDate(@Param("exchangeCode") String exchangeCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
/*
Mo Generating Logic For Type. There is a method in commonservice.
        resp.put("1", "Online");
        resp.put("2", "Account Payee");
        resp.put("3", "BEFTN");
        resp.put("4", "COC");
*/