package abl.frd.qremit.converter.nafex.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.URemitModel;
import java.util.*;
@Repository
public interface URemitModelRepository extends JpaRepository<URemitModel, Integer>{
    URemitModel findByTransactionNo(String transactionNo);
    Optional<URemitModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
