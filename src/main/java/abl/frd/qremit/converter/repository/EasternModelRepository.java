package abl.frd.qremit.converter.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.EasternModel;

@Repository
public interface EasternModelRepository extends JpaRepository<EasternModel, Integer>{
    EasternModel findByTransactionNo(String transactionNo);
    Optional<EasternModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
