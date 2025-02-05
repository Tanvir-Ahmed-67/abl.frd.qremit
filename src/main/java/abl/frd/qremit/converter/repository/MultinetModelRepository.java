package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.MultinetModel;



@Repository
public interface MultinetModelRepository extends JpaRepository<MultinetModel, Integer>{
    MultinetModel findByTransactionNo(String transactionNo);
    Optional<MultinetModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
