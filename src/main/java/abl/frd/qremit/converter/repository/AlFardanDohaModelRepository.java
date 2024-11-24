package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.AlFardanDohaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AlFardanDohaModelRepository extends JpaRepository<AlFardanDohaModel, Integer>{
    AlFardanDohaModel findByTransactionNo(String transactionNo);
    Optional<AlFardanDohaModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
