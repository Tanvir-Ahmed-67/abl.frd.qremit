package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.CityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CityModelRepository extends JpaRepository<CityModel, Integer>{
    CityModel findByTransactionNo(String transactionNo);
    Optional<CityModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
}
