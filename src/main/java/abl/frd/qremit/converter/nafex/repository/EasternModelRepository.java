package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.model.EasternModel;
import abl.frd.qremit.converter.nafex.model.EasternModel;


@Repository
public interface EasternModelRepository extends JpaRepository<EasternModel, Integer>{
    EasternModel findByTransactionNo(String transactionNo);
    Optional<EasternModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
