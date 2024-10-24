package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.FsieModel;
import java.util.*;
@Repository
public interface FsieModelRepository extends JpaRepository<FsieModel, Integer>{
    FsieModel findByTransactionNo(String transactionNo);
    Optional<FsieModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
