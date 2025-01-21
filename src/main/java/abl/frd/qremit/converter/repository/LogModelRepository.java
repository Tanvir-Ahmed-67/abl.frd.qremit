package abl.frd.qremit.converter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import abl.frd.qremit.converter.model.LogModel;

public interface LogModelRepository extends JpaRepository <LogModel, Integer>{
    LogModel findByDataId(String dataId);
}
