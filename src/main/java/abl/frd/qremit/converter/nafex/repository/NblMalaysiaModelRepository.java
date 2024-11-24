package abl.frd.qremit.converter.nafex.repository;
import abl.frd.qremit.converter.nafex.model.NblMalaysiaModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface NblMalaysiaModelRepository extends JpaRepository<NblMalaysiaModel, Integer> {
    NblMalaysiaModel findByTransactionNo(String transactionNo);
    Optional<NblMalaysiaModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
