package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AlzadeedModel;



@Repository
public interface AlzadeedModelRepository extends JpaRepository<AlzadeedModel, Integer>{
    AlzadeedModel findByTransactionNo(String transactionNo);
    Optional<AlzadeedModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
