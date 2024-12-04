package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.CityModel;
import java.util.*;

@Repository
public interface CityModelRepository extends JpaRepository<CityModel, Integer>{
    CityModel findByTransactionNo(String transactionNo);
    Optional<CityModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
