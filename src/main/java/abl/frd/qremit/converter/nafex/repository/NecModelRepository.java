package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.NecModel;


@Repository
public interface NecModelRepository extends JpaRepository<NecModel, Integer>{
    NecModel findByTransactionNo(String transactionNo);
    Optional<NecModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
