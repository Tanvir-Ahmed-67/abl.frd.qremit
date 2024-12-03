package abl.frd.qremit.converter.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import abl.frd.qremit.converter.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CustomQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    CommonService commonService;

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        String sql = "SELECT a.* FROM %s a, upload_file_info b WHERE a.file_info_model_id = b.id AND b.id = ?";
        String queryStr = String.format(sql, tableName,fileInfoId);
        Map<String, Object> params = new HashMap<>();
        params.put("1", fileInfoId);
        return commonService.getData(queryStr,params);
    }
    /*
    public Map<String,Object> getFileTotalAmount(String tableName, List<Integer> fileInfoId, String exchangeCode) {
        String fields = "b.exchange_code as exchangeCode, sum(a.amount) as totalAmount, COUNT(DISTINCT a.file_info_model_id) as cnt";
        fields += " ,SUM(DISTINCT CAST(IFNULL(b.online_count, '0') AS UNSIGNED)) AS onlineCount, SUM(DISTINCT CAST(IFNULL(b.account_payee_count, '0') AS UNSIGNED)) AS accountPayeeCount";
        fields += " ,SUM(DISTINCT CAST(IFNULL(b.coc_count, '0') AS UNSIGNED)) AS cocCount, SUM(DISTINCT CAST(IFNULL(b.beftn_count, '0') AS UNSIGNED)) AS beftnCount";
        fields += " ,SUM(error_count) AS errorCount";
        String sql = "SELECT " + fields + " FROM %s a INNER JOIN upload_file_info b ON a.file_info_model_id = b.id WHERE b.exchange_code = ? and a.file_info_model_id IN (%s)";
        String fileIds = fileInfoId.stream().map(id -> "?").collect(Collectors.joining(", "));
        String queryStr = String.format(sql, tableName,fileIds);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("1", exchangeCode);
        int j = 2;
        for(int i=0; i< fileInfoId.size(); i++){
            params.put(CommonService.convertIntToString(j), fileInfoId.get(i));
            j++;
        }
        System.out.println(queryStr + params);
        return commonService.getData(queryStr,params);
    }
    */
    public Map<String, Object> getFileTotalExchangeWise(String starDateTime, String endDateTime, int userId){
        String fields = "exchange_code as exchangeCode, REPLACE(FORMAT(SUM(total_amount), 2),',','')  as totalAmount, SUM(DISTINCT CAST(IFNULL(total_count, '0') AS UNSIGNED)) AS totalCount";
        fields += " ,SUM(DISTINCT CAST(IFNULL(online_count, '0') AS UNSIGNED)) AS onlineCount, SUM(DISTINCT CAST(IFNULL(account_payee_count, '0') AS UNSIGNED)) AS accountPayeeCount";
        fields += " ,SUM(DISTINCT CAST(IFNULL(coc_count, '0') AS UNSIGNED)) AS cocCount, SUM(DISTINCT CAST(IFNULL(beftn_count, '0') AS UNSIGNED)) AS beftnCount";
        fields += " ,SUM(error_count) AS errorCount";
        String sql = "SELECT " + fields + " FROM upload_file_info where upload_date_time BETWEEN ? AND ?";
        String extra = (userId != 0)   ?    " AND user_id=?":"";
        sql += extra + " GROUP BY exchange_code ORDER BY exchange_code";
        Map<String, Object> params = new HashMap<>();
        if(userId != 0) params.put("3", userId);
        params.put("1", starDateTime);
        params.put("2", endDateTime);
        return commonService.getData(sql, params);
    }

    public Map<String,Object> getRoutingDetails(String routingNo){
        Map<String, Object> params = new HashMap<>();
        String queryStr = String.format("SELECT * FROM routing_no where routing_no = ?", routingNo);
        params.put("1", routingNo);
        return commonService.getData(queryStr,params);
    }

}
