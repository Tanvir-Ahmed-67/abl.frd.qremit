package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.InstantCashModel;


@Repository
public interface InstantCashModelRepository extends JpaRepository<InstantCashModel, Integer>{
    InstantCashModel findByTransactionNo(String transactionNo);
    Optional<InstantCashModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
