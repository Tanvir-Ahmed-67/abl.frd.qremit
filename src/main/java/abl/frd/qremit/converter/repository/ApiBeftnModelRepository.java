package abl.frd.qremit.converter.repository;

import abl.frd.qremit.converter.model.ApiBeftnModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiBeftnModelRepository extends JpaRepository<ApiBeftnModel, Integer> {
    ApiBeftnModel findByTransactionNo(String transactionNo);
    Optional<ApiBeftnModel> findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String transactionNo, double amount, String exchangeCode);
    List<ApiBeftnModel> findAllByFileInfoModelId(int fileInfoModelId);
    Page<ApiBeftnModel> findByFileInfoModelId(int fileInfoModelId, Pageable pageable);
}
