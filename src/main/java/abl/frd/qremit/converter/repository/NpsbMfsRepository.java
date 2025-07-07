package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.NpsbMfsModel;

@Repository
public interface NpsbMfsRepository extends JpaRepository<NpsbMfsModel, Integer> {

}
