package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AgexSingaporeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgexSingaporeModelRepository extends JpaRepository<AgexSingaporeModel, Integer> {
}
