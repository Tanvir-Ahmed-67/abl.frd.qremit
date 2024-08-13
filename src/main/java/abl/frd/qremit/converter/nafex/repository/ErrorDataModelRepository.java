package abl.frd.qremit.converter.nafex.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.model.ErrorDataModel;

@Repository
public interface ErrorDataModelRepository extends JpaRepository<ErrorDataModel, Integer>{

    List<ErrorDataModel> findByUserModelId(int userId);
}
