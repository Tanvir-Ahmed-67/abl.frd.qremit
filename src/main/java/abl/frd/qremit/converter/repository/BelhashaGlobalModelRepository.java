package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.BelhashaGlobalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BelhashaGlobalModelRepository extends JpaRepository<BelhashaGlobalModel, Integer>{
    BelhashaGlobalModel findByTransactionNo(String transactionNo);
    Optional<BelhashaGlobalModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
