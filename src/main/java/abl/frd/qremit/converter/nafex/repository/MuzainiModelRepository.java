package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.MuzainiModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuzainiModelRepository extends JpaRepository<MuzainiModel, Integer> {
    /*
@Query("SELECT n FROM NafexEhMstModel n WHERE n.checkT24 = '1'")
    List<NafexEhMstModel> findAllNafexModelHavingOnlineAccount();
@Query("SELECT n FROM NafexEhMstModel n WHERE n.checkCoc = '1'")
    List<NafexEhMstModel> findAllNafexModelHavingCoc();
@Query("SELECT n FROM NafexEhMstModel n WHERE n.checkBeftn = '1'")
    List<NafexEhMstModel> findAllNafexModelHavingBeftn();
@Query("SELECT n FROM NafexEhMstModel n WHERE n.checkAccPayee = '1'")
    List<NafexEhMstModel> findAllNafexModelHavingAccountPayee();

     */
}
