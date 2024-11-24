package abl.frd.qremit.converter.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.AlRostamaniModel;

@Repository
public interface AlRostamaniModelRepository extends JpaRepository<AlRostamaniModel, Integer>{
    AlRostamaniModel findByTransactionNo(String transactionNo);
    Optional<AlRostamaniModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
