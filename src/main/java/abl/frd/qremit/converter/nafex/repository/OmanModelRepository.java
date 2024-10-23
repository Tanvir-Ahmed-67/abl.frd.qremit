package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.OmanModel;


@Repository
public interface OmanModelRepository extends JpaRepository<OmanModel, Integer>{
    OmanModel findByTransactionNo(String transactionNo);
    Optional<OmanModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
