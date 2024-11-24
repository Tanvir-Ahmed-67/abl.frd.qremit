package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.NblMalaysiaModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface NblMalaysiaModelRepository extends JpaRepository<NblMalaysiaModel, Integer> {
    NblMalaysiaModel findByTransactionNo(String transactionNo);
    Optional<NblMalaysiaModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
