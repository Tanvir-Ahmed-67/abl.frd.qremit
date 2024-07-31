package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoModelRepository extends JpaRepository<FileInfoModel, Integer> {
    //@Query(value = "SELECT * FROM upload_file_info  WHERE user_id = :userId", nativeQuery = true)
    @Query("SELECT n FROM FileInfoModel n WHERE n.userModel.id =:userId")
    List<FileInfoModel> getUploadedFileDetails(@Param("userId") int userId);
    FileInfoModel findByFileName(String fileName);
    @Query("UPDATE FileInfoModel n SET n.accountPayeeCount = :accountPayeeCount, n.beftnCount = :beftnCount, n.cocCount = :cocCount, n.onlineCount = :onlineCount, n.totalCount = :totalCount, n.processedCount = :processedCount, n.unprocessedCount = :unprocessedCount where n.id = :id")
    void updateFileInfoModel(long id, String accountPayeeCount, String beftnCount, String cocCount, String onlineCount, String totalCount, String processedCount, String unprocessedCount);
}
