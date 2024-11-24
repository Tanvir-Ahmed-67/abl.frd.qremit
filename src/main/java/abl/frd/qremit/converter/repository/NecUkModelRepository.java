package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.model.NecUkModel;


@Repository
public interface NecUkModelRepository extends JpaRepository<NecUkModel, Integer>{
    NecUkModel findByTransactionNo(String transactionNo);
    Optional<NecUkModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
