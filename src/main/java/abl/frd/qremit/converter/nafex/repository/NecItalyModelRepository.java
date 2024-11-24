package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.NecItalyModel;


@Repository
public interface NecItalyModelRepository extends JpaRepository<NecItalyModel, Integer>{
    NecItalyModel findByTransactionNo(String transactionNo);
    Optional<NecItalyModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
