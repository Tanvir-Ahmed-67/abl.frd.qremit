package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.NafexEhMstModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnlineModelRepository extends JpaRepository<OnlineModel, Integer> {
    @Query("SELECT n FROM OnlineModel n WHERE n.fileInfoModel.id =?1")
    List<OnlineModel> findAllOnlineModelHavingFileInfoId(long id);
}
