package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AlawnehModel;

@Repository
public interface AlawnehModelRepository extends JpaRepository<AlawnehModel, Integer>{
    AlawnehModel findByTransactionNo(String transactionNo);
    Optional<AlawnehModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
