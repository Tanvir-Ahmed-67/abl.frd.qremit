package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.GenericModel;
import java.util.*;

@Repository
public interface GenericModelRepository extends JpaRepository<GenericModel, Integer>{
    GenericModel findByTransactionNo(String transactionNo);
    Optional<GenericModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
