package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.MoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface MoModelRepository extends JpaRepository<MoModel, Integer> {
    @Query("SELECT n FROM MoModel n WHERE n.moDate = :moDate")
    MoModel findByMoGenerationDate(@Param("moDate") LocalDate moDate);
}
