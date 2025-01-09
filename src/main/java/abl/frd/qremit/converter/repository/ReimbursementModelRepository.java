package abl.frd.qremit.converter.repository;

import abl.frd.qremit.converter.model.ReimbursementModel;
import abl.frd.qremit.converter.model.ReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReimbursementModelRepository extends JpaRepository<ReimbursementModel, Integer> {
    @Query("SELECT n FROM ReimbursementModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate")
    List<ReimbursementModel> findAllReimbursementByDate(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    @Query("SELECT n FROM ReimbursementModel n WHERE n.reportDate BETWEEN :fromDate AND :toDate AND n.type='4'")
    List<ReimbursementModel> findAllCocReimbursementByDate(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    boolean existsByExchangeCodeAndTransactionNoAndMainAmountAndBeneficiaryAccount(String exchangeCode, String transactionNo, Double mainAmount, String beneficiaryAccount);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate BETWEEN :reimbursementStartDate AND :reimbursementEndDate AND n.isApi = 0 AND n.type IN ('2', '4')")
    List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(@Param("reimbursementStartDate") LocalDate reimbursementStartDate, @Param("reimbursementEndDate") LocalDate reimbursementEndDate);

}
