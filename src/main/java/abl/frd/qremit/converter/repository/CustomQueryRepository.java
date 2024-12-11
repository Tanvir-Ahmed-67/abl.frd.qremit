package abl.frd.qremit.converter.repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import abl.frd.qremit.converter.model.ApiBeftnModel;
import abl.frd.qremit.converter.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;

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

    public Map<String, Object> getFileTotalExchangeWise(String starDateTime, String endDateTime, int userId){
        String fields = "exchange_code, REPLACE(FORMAT(SUM(total_amount), 2),',','')  as totalAmount, SUM(CAST(IFNULL(total_count, '0') AS UNSIGNED)) AS totalCount";
        fields += " ,SUM(CAST(IFNULL(online_count, '0') AS UNSIGNED)) AS onlineCount, SUM(CAST(IFNULL(account_payee_count, '0') AS UNSIGNED)) AS accountPayeeCount";
        fields += " ,SUM(CAST(IFNULL(coc_count, '0') AS UNSIGNED)) AS cocCount, SUM(CAST(IFNULL(beftn_count, '0') AS UNSIGNED)) AS beftnCount";
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

    public Map<String, Object> calculateTotalAmountForConvertedModel(String tableName, int fileInfoModelId){
        String sql = "SELECT file_info_model_id, REPLACE(FORMAT(SUM(amount), 2),',','')  as totalAmount FROM %s WHERE file_info_model_id = ?";
        String queryStr = String.format(sql, tableName,fileInfoModelId);
        Map<String, Object> params = new HashMap<>();
        params.put("1", fileInfoModelId);
        return commonService.getData(queryStr,params);
    }

    public Map<String,Object> getRoutingDetails(String routingNo){
        Map<String, Object> params = new HashMap<>();
        String queryStr = String.format("SELECT * FROM routing_no where routing_no = ?", routingNo);
        params.put("1", routingNo);
        return commonService.getData(queryStr,params);
    }

    public Map<String,Object> getBranchDetailsFromSwiftCode(String swiftCode){
        Map<String, Object> params = new HashMap<>();
        String queryStr = "SELECT * FROM swift_code_to_branch_code  where swift_code = ?";
        params.put("1", swiftCode);
        return commonService.getData(queryStr,params);
    }
    
    public Map<String, Object> getBaseDataByTransactionNoAndAmountAndExchangeCodeIn(List<String[]> data, String tbl){
        tbl = "base_data_table_" + tbl;
        Map<String, Object> params = new HashMap<>();
        List<String> tuples = new ArrayList<>();
        for(String[] record: data){
            tuples.add(String.format("('%s', %s, '%s')", record[0], record[1], record[2]));
        }
        
        String sql = "SELECT * FROM %s WHERE (transaction_no, CAST(amount AS CHAR), exchange_code) IN ( ";
        String queryStr = String.format(sql, tbl);
        StringBuilder queryBuilder = new StringBuilder(queryStr);
        queryBuilder.append(String.join(", ", tuples)).append(")");
        return commonService.getData(queryBuilder.toString(),params); 
    }
        

}
