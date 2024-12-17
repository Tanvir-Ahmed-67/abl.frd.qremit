package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.KandHModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface KandHModelRepository extends JpaRepository<KandHModel, Integer>{
    KandHModel findByTransactionNo(String transactionNo);
    Optional<KandHModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
