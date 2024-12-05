package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.RiaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiaModelRepository extends JpaRepository<RiaModel, Integer> {
    Optional<RiaModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
