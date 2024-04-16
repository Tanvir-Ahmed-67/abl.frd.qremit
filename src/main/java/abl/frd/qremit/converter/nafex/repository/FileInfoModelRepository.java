package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoModelRepository extends JpaRepository<FileInfoModel, Integer> {
}
