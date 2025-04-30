package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.IpRange;
import java.util.*;
@Repository
public interface IpRangeRepository extends JpaRepository<IpRange, Integer>{
    List<IpRange> findAllByPublished(int published);
}