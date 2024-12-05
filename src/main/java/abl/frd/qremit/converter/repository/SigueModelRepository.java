package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.SigueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SigueModelRepository extends JpaRepository<SigueModel, Integer>{
    SigueModel findByTransactionNo(String transactionNo);
    Optional<SigueModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
