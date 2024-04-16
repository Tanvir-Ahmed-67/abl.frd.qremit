package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountPayeeModelRepository extends JpaRepository<AccountPayeeModel, Integer> {
    @Query("SELECT n FROM AccountPayeeModel n WHERE n.fileInfoModel.id =?1")
    List<AccountPayeeModel> findAllAccountPayeeModelHavingFileInfoId(long id);
}
