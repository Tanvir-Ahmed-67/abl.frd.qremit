package abl.frd.qremit.converter.nafex.repository;
import abl.frd.qremit.converter.nafex.model.NblUsaModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface NblUsaModelRepository extends JpaRepository<NblUsaModel, Integer> {
    NblUsaModel findByTransactionNo(String transactionNo);
    Optional<NblUsaModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
