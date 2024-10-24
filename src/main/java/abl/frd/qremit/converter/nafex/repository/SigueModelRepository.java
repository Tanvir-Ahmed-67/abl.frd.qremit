package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import abl.frd.qremit.converter.nafex.model.SigueModel;


@Repository
public interface SigueModelRepository extends JpaRepository<SigueModel, Integer>{
    SigueModel findByTransactionNo(String transactionNo);
    Optional<SigueModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
