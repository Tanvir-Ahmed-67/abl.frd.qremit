package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.AlFardanAbuDhabiModel;
import java.util.*;
@Repository
public interface AlFardanAbuDhabiModelRepository extends JpaRepository<AlFardanAbuDhabiModel, Integer>{
    AlFardanAbuDhabiModel findByTransactionNo(String transactionNo);
    Optional<AlFardanAbuDhabiModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
