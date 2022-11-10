package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.NafexEhMstModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineModelRepository extends JpaRepository<OnlineModel, Integer> {
}
