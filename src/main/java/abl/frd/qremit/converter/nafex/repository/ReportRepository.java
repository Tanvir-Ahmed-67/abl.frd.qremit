package abl.frd.qremit.converter.nafex.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import abl.frd.qremit.converter.nafex.service.CommonService;
import java.util.*;

@Repository
public class ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    CommonService commonService;

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        String queryStr = String.format("SELECT a.* FROM %s a, upload_file_info b WHERE a.file_info_model_id = b.id AND b.id = ?", tableName,fileInfoId);
        Map<String, Object> params = new HashMap<>();
        params.put("1", fileInfoId);
        return commonService.getData(queryStr,params);
    }

    public Map<String,Object> getRoutingDetails(String routingNo){
        Map<String, Object> params = new HashMap<>();
        String queryStr = String.format("SELECT * FROM routing_no where routing_no = ?", routingNo);
        params.put("1", routingNo);
        return commonService.getData(queryStr,params);
    }

}
