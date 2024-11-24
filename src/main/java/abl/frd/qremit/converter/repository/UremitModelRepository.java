package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.UremitModel;
import java.util.*;
@Repository
public interface UremitModelRepository extends JpaRepository<UremitModel, Integer>{
    UremitModel findByTransactionNo(String transactionNo);
    Optional<UremitModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
