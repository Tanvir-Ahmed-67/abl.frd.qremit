package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AlZamanModel;

@Repository
public interface AlZamanModelRepository extends JpaRepository<AlZamanModel, Integer>{
    AlZamanModel findByTransactionNo(String transactionNo);
    Optional<AlZamanModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
