package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.model.ArhMalaysiaModel;


@Repository
public interface ArhMalaysiaModelRepository extends JpaRepository<ArhMalaysiaModel, Integer>{
    ArhMalaysiaModel findByTransactionNo(String transactionNo);
    Optional<ArhMalaysiaModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
