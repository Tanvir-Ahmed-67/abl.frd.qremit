package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.EzRemitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface EzRemitModelRepository extends JpaRepository<EzRemitModel, Integer> {
    Optional<EzRemitModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
