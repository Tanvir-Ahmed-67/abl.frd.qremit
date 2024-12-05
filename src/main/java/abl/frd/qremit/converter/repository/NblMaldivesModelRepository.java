package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.NblMaldivesModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface NblMaldivesModelRepository extends JpaRepository<NblMaldivesModel, Integer> {
    NblMaldivesModel findByTransactionNo(String transactionNo);
    Optional<NblMaldivesModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
