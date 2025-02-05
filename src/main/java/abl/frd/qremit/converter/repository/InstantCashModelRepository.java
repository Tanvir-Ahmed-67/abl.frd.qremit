package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.InstantCashModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InstantCashModelRepository extends JpaRepository<InstantCashModel, Integer>{
    InstantCashModel findByTransactionNo(String transactionNo);
    Optional<InstantCashModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
