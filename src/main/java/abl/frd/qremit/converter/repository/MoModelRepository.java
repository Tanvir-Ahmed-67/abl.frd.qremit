package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.MoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoModelRepository extends JpaRepository<MoModel, Integer> {
    @Query("SELECT n FROM MoModel n WHERE n.moDate = :moDate")
    MoModel findByMoGenerationDate(@Param("moDate") LocalDate moDate);
    @Query(value = "SELECT MAX(CAST(SUBSTRING_INDEX(mo_number, '-', -1) AS UNSIGNED)) " +
            "FROM mo WHERE mo_number LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    Long findMaxMoNumberSuffix(@Param("prefix") String prefix);
    @Query("SELECT n From MoModel n WHERE n.moDate BETWEEN :fromDate AND :toDate")
    List<MoModel> findCmoByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}