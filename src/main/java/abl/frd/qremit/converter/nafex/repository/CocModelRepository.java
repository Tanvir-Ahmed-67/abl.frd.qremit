package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CocModelRepository extends JpaRepository<CocModel, Integer> {
    @Query("SELECT n FROM CocModel n WHERE n.fileInfoModel.id =?1")
    List<CocModel> findAllCocModelHavingFileInfoId(int id);
    @Query("SELECT n FROM CocModel n")
    List<CocModel> findAllCocModel();
    Integer countByIsProcessed(int isProcessed);
    Integer countByIsDownloaded(int isDownloaded);

    @Query("SELECT n FROM CocModel n WHERE n.isProcessed= :isProcessed and n.isDownloaded= :isDownloaded")
    List<CocModel> loadUnprocessedCocData(@Param("isProcessed") int isProcessed, @Param("isDownloaded") int isDownloaded);
}
