package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.MerchantradeModel;

@Repository
public interface MerchantradeModelRepository extends JpaRepository<MerchantradeModel, Integer>{
    MerchantradeModel findByTransactionNo(String transactionNo);
    Optional<MerchantradeModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
