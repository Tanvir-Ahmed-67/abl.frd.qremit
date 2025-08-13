package abl.frd.qremit.converter.repository;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.model.BeftnReturnModel;
import java.util.*;

@Repository
public interface BeftnReturnRepository extends JpaRepository<BeftnReturnModel, Integer>{
    @Query("SELECT n FROM BeftnReturnModel n WHERE n.exchangeCode = :exchangeCode AND n.processedDate BETWEEN :startDate AND :endDate ORDER BY n.processedDate")
    List<BeftnReturnModel> getBeftnReturnModelByExchangeCodeAndProcessedDate(@Param("exchangeCode") String exchangeCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT n FROM BeftnReturnModel n WHERE n.exchangeCode = :exchangeCode AND n.returnDate BETWEEN :startDate AND :endDate ORDER BY n.exchangeCode DESC")
    List<BeftnReturnModel> getBeftnReturnModelByExchangeCodeAndReturnDate(@Param("exchangeCode") String exchangeCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT n FROM BeftnReturnModel n WHERE n.fileInfoModelId = :fileInfoModelId ORDER BY n.exchangeCode DESC")
    List<BeftnReturnModel> getBeftnReturnModelByFileInfoModelId(@Param("fileInfoModelId") int fileInfoModelId);
}
