package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoModelRepository extends JpaRepository<FileInfoModel, Integer> {
    //@Query(value = "SELECT * FROM upload_file_info  WHERE user_id = :userId", nativeQuery = true)
    @Query("SELECT n FROM FileInfoModel n WHERE n.userModel.id =:userId")
    List<FileInfoModel> getUploadedFileDetails(@Param("userId") int userId);
}
