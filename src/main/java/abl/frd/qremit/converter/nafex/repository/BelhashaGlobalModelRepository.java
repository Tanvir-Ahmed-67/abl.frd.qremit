package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.BelhashaGlobalModel;


@Repository
public interface BelhashaGlobalModelRepository extends JpaRepository<BelhashaGlobalModel, Integer>{
    BelhashaGlobalModel findByTransactionNo(String transactionNo);
    Optional<BelhashaGlobalModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
