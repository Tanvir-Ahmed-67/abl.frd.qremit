package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AnbModel;
import java.util.*;
@Repository
public interface AnbModelRepository  extends JpaRepository<AnbModel, Integer>{
    AnbModel findByTransactionNo(String transactionNo);
    Optional<AnbModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
