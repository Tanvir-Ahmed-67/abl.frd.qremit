package abl.frd.qremit.converter.nafex.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.NblMalyasiaModel;

@Repository
public interface NblMalyasiaModelRepository extends JpaRepository<NblMalyasiaModel, Integer>{
    NblMalyasiaModel findByTransactionNo(String transactionNo);
    Optional<NblMalyasiaModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
