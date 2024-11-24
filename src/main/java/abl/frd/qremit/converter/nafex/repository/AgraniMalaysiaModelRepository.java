package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.AgraniMalaysiaModel;


@Repository
public interface AgraniMalaysiaModelRepository extends JpaRepository<AgraniMalaysiaModel, Integer>{
    AgraniMalaysiaModel findByTransactionNo(String transactionNo);
    Optional<AgraniMalaysiaModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
