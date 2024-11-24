package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.AmanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AmanModelRepository extends JpaRepository<AmanModel, Integer>{
    AmanModel findByTransactionNo(String transactionNo);
    Optional<AmanModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
