package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountPayeeModelRepository extends JpaRepository<AccountPayeeModel, Integer> {
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.fileInfoModel.id =?1")
    List<AccountPayeeModel> findAllAccountPayeeModelHavingFileInfoId(long id);
    @Query("SELECT n FROM AccountPayeeModel n")
    List<AccountPayeeModel> findAllAccountPayeeModel();
    Integer countByIsProcessed(String isProcessed);
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.isProcessed= :isProcessed")
    List<AccountPayeeModel> loadUnprocessedAccountPayeeData(@Param("isProcessed") String isProcessed);
}
