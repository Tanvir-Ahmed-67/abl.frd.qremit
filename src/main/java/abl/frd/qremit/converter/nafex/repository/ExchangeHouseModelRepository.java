package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeHouseModelRepository extends JpaRepository<ExchangeHouseModel, Integer> {
}
