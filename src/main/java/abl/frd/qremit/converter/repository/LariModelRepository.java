package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.LariModel;
import java.util.*;

@Repository
public interface LariModelRepository extends JpaRepository<LariModel, Integer>{
    LariModel findByTransactionNo(String transactionNo);
    //Optional<LariModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
    Optional<LariModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}