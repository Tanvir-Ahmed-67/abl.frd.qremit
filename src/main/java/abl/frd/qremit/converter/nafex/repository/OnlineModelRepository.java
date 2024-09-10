package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.NafexEhMstModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnlineModelRepository extends JpaRepository<OnlineModel, Integer> {
    @Query("SELECT n FROM OnlineModel n WHERE n.fileInfoModel.id =?1")
    List<OnlineModel> findAllOnlineModelHavingFileInfoId(int id);
    @Query("SELECT n FROM OnlineModel n")
    List<OnlineModel> findAllOnlineModel();
    @Query("SELECT n FROM OnlineModel n WHERE n.isProcessed= :isProcessed")
    List<OnlineModel> loadUnprocessedOnlineData(@Param("isProcessed") String isProcessed);
    @Query("SELECT n FROM OnlineModel n WHERE n.isProcessed= :isProcessed")
    List<OnlineModel> loadProcessedOnlineData(@Param("isProcessed") String isProcessed);
    Integer countByIsProcessed(String isProcessed);

}
