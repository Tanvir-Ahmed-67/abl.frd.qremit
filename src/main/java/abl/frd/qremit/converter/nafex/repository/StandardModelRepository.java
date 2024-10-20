package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.StandardModel;


@Repository
public interface StandardModelRepository extends JpaRepository<StandardModel, Integer>{
    StandardModel findByTransactionNo(String transactionNo);
    Optional<StandardModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
