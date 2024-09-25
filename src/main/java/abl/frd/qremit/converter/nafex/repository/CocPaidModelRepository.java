package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AgexSingaporeModel;
import abl.frd.qremit.converter.nafex.model.CocPaidModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CocPaidModelRepository extends JpaRepository<CocPaidModel, Integer> {
    Optional<CocPaidModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
