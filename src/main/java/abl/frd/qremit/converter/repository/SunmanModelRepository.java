package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.model.SunmanModel;


@Repository
public interface SunmanModelRepository extends JpaRepository<SunmanModel, Integer>{
    SunmanModel findByTransactionNo(String transactionNo);
    Optional<SunmanModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
