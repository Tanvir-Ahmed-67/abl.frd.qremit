package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.AgexSingaporeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgexSingaporeModelRepository extends JpaRepository<AgexSingaporeModel, Integer> {
    Optional<AgexSingaporeModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
