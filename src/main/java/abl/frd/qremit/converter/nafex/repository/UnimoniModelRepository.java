package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.UnimoniModel;


@Repository
public interface UnimoniModelRepository extends JpaRepository<UnimoniModel, Integer>{
    UnimoniModel findByTransactionNo(String transactionNo);
    Optional<UnimoniModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
