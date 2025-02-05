package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.SaibModel;
import java.util.*;

@Repository
public interface SaibModelRepository extends JpaRepository<SaibModel, Integer> {
    SaibModel findByTransactionNo(String transactionNo);
    Optional<SaibModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
