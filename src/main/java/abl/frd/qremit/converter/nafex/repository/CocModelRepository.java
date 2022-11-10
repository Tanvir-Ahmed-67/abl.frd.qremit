package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocModelRepository extends JpaRepository<CocModel, Integer> {
}
