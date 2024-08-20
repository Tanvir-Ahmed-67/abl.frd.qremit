package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AgexSingaporeModel;
import abl.frd.qremit.converter.nafex.model.EzRemitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EzRemitModelRepository extends JpaRepository<EzRemitModel, Integer> {
    Optional<EzRemitModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
