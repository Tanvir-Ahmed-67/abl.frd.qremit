package abl.frd.qremit.converter.nafex.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.OmanKuwaitModel;


@Repository
public interface OmanKuwaitModelRepository extends JpaRepository<OmanKuwaitModel, Integer>{
    OmanKuwaitModel findByTransactionNo(String transactionNo);
    Optional<OmanKuwaitModel> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
