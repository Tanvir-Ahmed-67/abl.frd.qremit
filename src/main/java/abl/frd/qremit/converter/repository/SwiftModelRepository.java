package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.SwiftModel;




@Repository
public interface SwiftModelRepository extends JpaRepository<SwiftModel, Integer>{
    SwiftModel findByTransactionNo(String transactionNo);
    Optional<SwiftModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
