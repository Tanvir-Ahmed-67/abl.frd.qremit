package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AlBiladModel;

@Repository
public interface AlBiladModelRepository extends JpaRepository<AlBiladModel, Integer>{
    AlBiladModel findByTransactionNo(String transactionNo);
    Optional<AlBiladModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}