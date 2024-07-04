package abl.frd.qremit.converter.nafex.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.ReportService;

import java.util.Map;

@Repository
public class ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    CommonService commonService;

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        String queryStr = String.format("SELECT a.* FROM %s a, upload_file_info b WHERE a.file_info_model_id = b.id AND b.id = %s", tableName,fileInfoId);
        //System.out.println(queryStr);
        return commonService.getData(queryStr);
    }

}
