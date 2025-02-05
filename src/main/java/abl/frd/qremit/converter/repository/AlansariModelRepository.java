package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.AlansariModel;



@Repository
public interface AlansariModelRepository extends JpaRepository<AlansariModel, Integer>{
    AlansariModel findByTransactionNo(String transactionNo);
    Optional<AlansariModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
