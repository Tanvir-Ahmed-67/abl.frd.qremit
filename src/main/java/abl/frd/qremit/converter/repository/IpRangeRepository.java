package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import abl.frd.qremit.converter.model.IpRange;
import java.util.*;

public interface IpRangeRepository extends JpaRepository<IpRange, Integer>{
    List<IpRange> findAllByPublished(int published);
}