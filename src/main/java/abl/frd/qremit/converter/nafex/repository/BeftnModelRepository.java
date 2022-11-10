package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.BeftnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeftnModelRepository extends JpaRepository<BeftnModel, Integer> {
}
