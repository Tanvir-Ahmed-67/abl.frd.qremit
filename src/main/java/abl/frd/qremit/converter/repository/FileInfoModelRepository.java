package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.FileInfoModelDTO;
import abl.frd.qremit.converter.model.FileInfoModel;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoModelRepository extends JpaRepository<FileInfoModel, Integer> {
    @Query("SELECT n FROM FileInfoModel n WHERE n.userModel.id =:userId and n.uploadDateTime BETWEEN :startDate AND :endDate ORDER BY n.exchangeCode")
    List<FileInfoModel> getUploadedFileDetails(@Param("userId") int userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT n FROM FileInfoModel n WHERE n.uploadDateTime BETWEEN :startDate AND :endDate ORDER BY n.exchangeCode")
    List<FileInfoModel> getUploadedFileDetails(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    FileInfoModel findByFileName(String fileName);
    FileInfoModel findById(int id);
    //@Query("UPDATE FileInfoModel n SET n.accountPayeeCount = :accountPayeeCount, n.beftnCount = :beftnCount, n.cocCount = :cocCount, n.onlineCount = :onlineCount, n.totalCount = :totalCount, n.isSettlement = :isSettlement, n.unprocessedCount = :unprocessedCount where n.id = :id")
    //void updateFileInfoModel(int id, String accountPayeeCount, String beftnCount, String cocCount, String onlineCount, String totalCount, int isSettlement, String unprocessedCount);
    @Query("SELECT n FROM FileInfoModel n WHERE n.uploadDateTime BETWEEN :startDate AND :endDate")
    List<FileInfoModel> getFileDetailsBetweenUploadedDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT new abl.frd.qremit.converter.model.FileInfoModelDTO(n, COUNT(n)) FROM FileInfoModel n WHERE n.exchangeCode= :exchangeCode AND n.isSettlement = :isSettlement AND n.uploadDateTime BETWEEN :startDate AND :endDate GROUP BY exchangeCode")
    FileInfoModelDTO getSettlementDataByExchangeCode(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("exchangeCode") String exchnageCode, @Param("isSettlement") int isSettlement);
}
