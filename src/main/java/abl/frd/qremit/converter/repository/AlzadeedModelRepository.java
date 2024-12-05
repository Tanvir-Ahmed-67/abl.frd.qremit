package abl.frd.qremit.converter.repository;
import java.util.*;

import abl.frd.qremit.converter.model.AlzadeedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AlzadeedModelRepository extends JpaRepository<AlzadeedModel, Integer>{
    AlzadeedModel findByTransactionNo(String transactionNo);
    Optional<AlzadeedModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
