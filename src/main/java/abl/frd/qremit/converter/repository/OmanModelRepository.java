package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.model.OmanModel;


@Repository
public interface OmanModelRepository extends JpaRepository<OmanModel, Integer>{
    OmanModel findByTransactionNo(String transactionNo);
    Optional<OmanModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
