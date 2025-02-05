package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.MerchantradeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantradeModelRepository extends JpaRepository<MerchantradeModel, Integer>{
    MerchantradeModel findByTransactionNo(String transactionNo);
    Optional<MerchantradeModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
