package abl.frd.qremit.converter.nafex.repository;
import abl.frd.qremit.converter.nafex.model.ApiBeftnModel;
import abl.frd.qremit.converter.nafex.model.ApiT24Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ApiT24ModelRepository extends JpaRepository<ApiT24Model, Integer> {
    ApiT24Model findByTransactionNo(String transactionNo);
    Optional<ApiT24Model> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
    List<ApiT24Model> findAllByFileInfoModelId(int fileInfoModelId);
    Page<ApiT24Model> findAllByFileInfoModelId(int fileInfoModelId, Pageable pageable);
}
