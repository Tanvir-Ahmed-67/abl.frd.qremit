package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.IndexModel;
import java.util.*;

@Repository
public interface IndexModelRepository extends JpaRepository<IndexModel, Integer>{
    IndexModel findByTransactionNo(String transactionNo);
    Optional<IndexModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
