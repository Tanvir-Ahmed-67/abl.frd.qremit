package abl.frd.qremit.converter.nafex.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import abl.frd.qremit.converter.nafex.model.ReportModel;

public interface ReportModelRepository  extends JpaRepository<ReportModel, Integer>{
    Optional<ReportModel> findByExchangeCodeAndTransactionNoAndAmount(String exchangeCode, String transactionNo, Double amount);
}
