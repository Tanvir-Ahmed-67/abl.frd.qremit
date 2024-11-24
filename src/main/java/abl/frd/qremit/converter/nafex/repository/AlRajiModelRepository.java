package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import abl.frd.qremit.converter.nafex.model.AlRajiModel;

public interface AlRajiModelRepository extends JpaRepository<AlRajiModel, Integer>{
    AlRajiModel findByTransactionNo(String transactionNo);
    Optional<AlRajiModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
