package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.BeftnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeftnModelRepository extends JpaRepository<BeftnModel, Integer> {
    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id =?1")
    List<BeftnModel> findAllBeftnModelHavingFileInfoId(long id);
}
