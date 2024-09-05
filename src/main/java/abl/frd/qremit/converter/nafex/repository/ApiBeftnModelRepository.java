package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.ApiBeftnModel;
import abl.frd.qremit.converter.nafex.model.BecModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiBeftnModelRepository extends JpaRepository<ApiBeftnModel, Long> {
    ApiBeftnModel findByTransactionNo(String transactionNo);
    Optional<ApiBeftnModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
