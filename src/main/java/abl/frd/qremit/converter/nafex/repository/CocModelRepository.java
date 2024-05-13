package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CocModelRepository extends JpaRepository<CocModel, Integer> {
    @Query("SELECT n FROM CocModel n WHERE n.fileInfoModel.id =?1")
    List<CocModel> findAllCocModelHavingFileInfoId(long id);
    @Query("SELECT n FROM CocModel n")
    List<CocModel> findAllCocModel();
    Integer countByIsProcessed(String isProcessed);
}
