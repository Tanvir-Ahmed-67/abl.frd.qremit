package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AmanModel;


@Repository
public interface AmanModelRepository extends JpaRepository<AmanModel, Integer>{
    AmanModel findByTransactionNo(String transactionNo);
    Optional<AmanModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
