package abl.frd.qremit.converter.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.ProgotiModel;

@Repository
public interface ProgotiModelRepository extends JpaRepository<ProgotiModel, Integer>{
    ProgotiModel findByTransactionNo(String transactionNo);
    Optional<ProgotiModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
