package abl.frd.qremit.converter.nafex.repository;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import abl.frd.qremit.converter.nafex.model.ReportModel;

public interface ReportModelRepository  extends JpaRepository<ReportModel, Integer>{
    Optional<ReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate = :reportDate")
    List<ReportModel> getReportModelByReportDate(@Param("reportDate") LocalDate reportDate);
}
