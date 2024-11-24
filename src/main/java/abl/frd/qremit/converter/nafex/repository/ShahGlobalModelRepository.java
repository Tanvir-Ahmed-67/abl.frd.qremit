package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.ShahGlobalModel;
import java.util.*;

@Repository
public interface ShahGlobalModelRepository extends JpaRepository<ShahGlobalModel, Integer>{
    ShahGlobalModel findByTransactionNo(String transactionNo);
    Optional<ShahGlobalModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
