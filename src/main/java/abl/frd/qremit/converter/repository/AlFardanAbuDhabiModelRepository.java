package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.AlFardanAbuDhabiModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public interface AlFardanAbuDhabiModelRepository extends JpaRepository<AlFardanAbuDhabiModel, Integer>{
    AlFardanAbuDhabiModel findByTransactionNo(String transactionNo);
    Optional<AlFardanAbuDhabiModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
