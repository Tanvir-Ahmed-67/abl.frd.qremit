package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.UnimoniModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UnimoniModelRepository extends JpaRepository<UnimoniModel, Integer>{
    UnimoniModel findByTransactionNo(String transactionNo);
    Optional<UnimoniModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
