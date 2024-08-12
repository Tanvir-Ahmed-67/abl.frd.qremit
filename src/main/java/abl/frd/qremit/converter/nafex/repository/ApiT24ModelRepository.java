package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.ApiBeftnModel;
import abl.frd.qremit.converter.nafex.model.ApiT24Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiT24ModelRepository extends JpaRepository<ApiT24Model, Long> {
    ApiT24Model findByTransactionNo(String transactionNo);
    Optional<ApiT24Model> findByTransactionNoEqualsIgnoreCase(String transactionNo);
}
