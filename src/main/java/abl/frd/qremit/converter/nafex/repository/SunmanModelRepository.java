package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.SunmanModel;


@Repository
public interface SunmanModelRepository extends JpaRepository<SunmanModel, Integer>{
    SunmanModel findByTransactionNo(String transactionNo);
    Optional<SunmanModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
