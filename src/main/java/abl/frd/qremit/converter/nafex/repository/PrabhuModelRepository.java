package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.PrabhuModel;
import java.util.*;

@Repository
public interface PrabhuModelRepository extends JpaRepository<PrabhuModel, Integer>{
    PrabhuModel findByTransactionNo(String transactionNo);
    Optional<PrabhuModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
