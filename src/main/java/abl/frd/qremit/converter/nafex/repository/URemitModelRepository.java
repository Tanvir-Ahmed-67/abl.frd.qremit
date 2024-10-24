package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.UremitModel;
import java.util.*;
@Repository
public interface UremitModelRepository extends JpaRepository<UremitModel, Integer>{
    UremitModel findByTransactionNo(String transactionNo);
    Optional<UremitModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
