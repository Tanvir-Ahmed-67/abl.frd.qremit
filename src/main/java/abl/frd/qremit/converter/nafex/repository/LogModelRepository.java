package abl.frd.qremit.converter.nafex.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import abl.frd.qremit.converter.nafex.model.LogModel;

public interface LogModelRepository extends JpaRepository <LogModel, Integer>{
    LogModel findByErrorDataId(String errorDataId);
}
