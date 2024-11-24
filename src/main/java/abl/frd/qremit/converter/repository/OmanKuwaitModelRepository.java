package abl.frd.qremit.converter.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.model.OmanKuwaitModel;


@Repository
public interface OmanKuwaitModelRepository extends JpaRepository<OmanKuwaitModel, Integer>{
    OmanKuwaitModel findByTransactionNo(String transactionNo);
    Optional<OmanKuwaitModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
