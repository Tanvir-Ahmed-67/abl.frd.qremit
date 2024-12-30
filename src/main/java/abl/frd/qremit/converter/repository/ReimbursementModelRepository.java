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
    @Query("SELECT n FROM ReimbursementModel n WHERE n.reimbursementDate = :reimbursementDate")
    List<ReimbursementModel> findAllReimbursementByDate(@Param("reimbursementDate") LocalDate reimbursementDate);
    @Query("SELECT n FROM ReimbursementModel n WHERE n.reimbursementDate = :reimbursementDate AND n.type='4'")
    List<ReimbursementModel> findAllCocReimbursementByDate(@Param("reimbursementDate") LocalDate reimbursementDate);

    boolean existsByExchangeCodeAndTransactionNoAndMainAmountAndBeneficiaryAccount(String exchangeCode, String transactionNo, Double mainAmount, String beneficiaryAccount);
    @Query("SELECT n FROM ReportModel n WHERE n.reportDate = :reimbursementDate AND n.isApi=0 AND n.type IN ('2', '4')")
    List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(@Param("reimbursementDate") LocalDate reimbursementDate);
}
