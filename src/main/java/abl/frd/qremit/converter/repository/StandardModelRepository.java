package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.StandardModel;


@Repository
public interface StandardModelRepository extends JpaRepository<StandardModel, Integer>{
    StandardModel findByTransactionNo(String transactionNo);
    Optional<StandardModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
