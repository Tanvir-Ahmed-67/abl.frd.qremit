package abl.frd.qremit.converter.nafex.repository;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.ErrorDataModel;

@Repository
public interface ErrorDataModelRepository extends JpaRepository<ErrorDataModel, Integer>{

    List<ErrorDataModel> findByUserModelId(int userId);
    List<ErrorDataModel> findByUserModelIdAndUpdateStatus(int userId, int updateStatus);
    List<ErrorDataModel> findByUpdateStatus(int updateStatus);
    ErrorDataModel findById(int id);
    @Transactional
    @Modifying
    @Query("UPDATE ErrorDataModel e SET e.updateStatus= :updateStatus where e.id= :id")
    void updateUpdateStatusById(int id, int updateStatus);
}
