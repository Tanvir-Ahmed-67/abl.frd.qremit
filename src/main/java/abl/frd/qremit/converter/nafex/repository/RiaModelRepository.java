package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.RiaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiaModelRepository extends JpaRepository<RiaModel, Integer> {
}
