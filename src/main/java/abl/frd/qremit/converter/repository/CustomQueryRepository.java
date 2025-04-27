package abl.frd.qremit.converter.repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import abl.frd.qremit.converter.service.CommonService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;
    private final DataSource dataSource;
    public CustomQueryRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Map<String,Object> getData(String sql, Map<String, Object> params){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        try(Connection con = dataSource.getConnection();         
            PreparedStatement pstmt = con.prepareStatement(sql)){
            int j = 1;
            for (Object value : params.values()) {
                pstmt.setObject(j++, value);
            }
            
            try(ResultSet rs = pstmt.executeQuery()){
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                if(!rs.next()){
                    return CommonService.getResp(1,"No data found",rows);
                }else{
                    do{
                        Map<String,Object> row = new HashMap<>();
                        for(int i = 1; i<= columnsNumber; i++) {
                            String columnName = rsmd.getColumnName(i);
                            Object columnValue = rs.getObject(i);
                            row.put(columnName, columnValue);
                        }
                        rows.add(row);
                    }while(rs.next());
                    return CommonService.getResp(0, "Data Found", rows);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            resp = CommonService.getResp(1, "An error occurred: " + e.getMessage(), rows);
        }
        return resp;
    }

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        String sql = "SELECT a.* FROM %s a, upload_file_info b WHERE a.file_info_model_id = b.id AND b.id = ?";
        String queryStr = String.format(sql, tableName,fileInfoId);
        Map<String, Object> params = new HashMap<>();
        params.put("1", fileInfoId);
        return getData(queryStr,params);
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
        return getData(sql, params);
    }

    public Map<String, Object> calculateTotalAmountForConvertedModel(String tableName, int fileInfoModelId){
        String sql = "SELECT file_info_model_id, REPLACE(FORMAT(SUM(amount), 2),',','')  as totalAmount FROM %s WHERE file_info_model_id = ?";
        String queryStr = String.format(sql, tableName,fileInfoModelId);
        Map<String, Object> params = new HashMap<>();
        params.put("1", fileInfoModelId);
        return getData(queryStr,params);
    }

    public Map<String,Object> getRoutingDetails(String routingNo, String bankCode){
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryStr = new StringBuilder("SELECT * FROM routing_no");
        boolean hasRoutingNo = routingNo != null && !routingNo.trim().isEmpty();
        boolean hasBankCode = bankCode != null && !bankCode.trim().isEmpty();

        if (hasRoutingNo || hasBankCode) {
            queryStr.append(" WHERE");
            if (hasRoutingNo) {
                queryStr.append(" routing_no = ?");
                params.put("1", routingNo);
            }
            if (hasBankCode) {
                if (hasRoutingNo) {
                    queryStr.append(" AND");
                }
                queryStr.append(" bank_code = ?");
                params.put(hasRoutingNo ? "2" : "1", bankCode);
            }
        }
        return getData(queryStr.toString(),params);
    }
    

    public Map<String,Object> getBranchDetailsFromSwiftCode(String swiftCode){
        Map<String, Object> params = new HashMap<>();
        String queryStr = "SELECT * FROM swift_code_to_branch_code  where swift_code = ?";
        params.put("1", swiftCode);
        return getData(queryStr,params);
    }
    
    public Map<String, Object> getUniqueListByTransactionNoAndAmountAndExchangeCodeIn(List<String[]> data, String tbl){
        tbl = "base_data_table_" + tbl;
        return generateUniqueTransactionSql(data, tbl, "CAST(amount AS CHAR)");
        /*
        Map<String, Object> params = new HashMap<>();
        List<String> tuples = new ArrayList<>();
        for(String[] record: data){
            tuples.add(String.format("('%s', %s, '%s')", record[0], record[1], record[2]));
        }
        
        String sql = "SELECT * FROM %s WHERE (transaction_no, CAST(amount AS CHAR), exchange_code) IN ( ";
        String queryStr = String.format(sql, tbl);
        StringBuilder queryBuilder = new StringBuilder(queryStr);
        queryBuilder.append(String.join(", ", tuples)).append(")");
        return getData(queryBuilder.toString(),params);
        */ 
    }

    public Map<String, Object> getArchiveUniqueList(List<String[]> data, String year){
        String tbl = "qremit_archive_" + year;
        return generateUniqueTransactionSql(data, tbl, "amount");
    }

    public Map<String, Object> generateUniqueTransactionSql(List<String[]> data, String tbl, String amountField){
        Map<String, Object> params = new HashMap<>();
        List<String> tuples = new ArrayList<>();
        for(String[] record: data){
            tuples.add(String.format("('%s', %s, '%s')", record[0], record[1], record[2]));
        }
        
        String sql = "SELECT * FROM %s WHERE (transaction_no, " + amountField + " , exchange_code) IN ( ";
        String queryStr = String.format(sql, tbl);
        StringBuilder queryBuilder = new StringBuilder(queryStr);
        queryBuilder.append(String.join(", ", tuples)).append(")");
        return getData(queryBuilder.toString(),params);
    }

    @Transactional
    public Map<String, Object> deleteByFileInfoModelId(String entityName, int fileInfoModelId){
        Map<String, Object> resp = CommonService.getResp(1, "Data not deleted", null);
        try{
            Query query = entityManager.createQuery("DELETE FROM " + entityName + " e WHERE e.fileInfoModel.id = :fileInfoModelId");
            query.setParameter("fileInfoModelId", fileInfoModelId);
            int affectedRows = query.executeUpdate();
            resp = CommonService.getResp(0, "Data deleted successful", null);
            resp.put("affectedRows", affectedRows);
        }catch(Exception e){
            e.printStackTrace();
            return CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }

    public Map<String, Object> getBeftnIncentiveNotProcessing(String[] keywords){
        StringBuilder whereClause = new StringBuilder();
        for(String keyword: keywords){
            if(whereClause.length() > 0){
                whereClause.append(" OR ");
            }
            whereClause.append("beneficiary_name LIKE '%").append(keyword).append("%'");
        }
        String specialCase = " OR beneficiary_name LIKE ' FARM%' OR beneficiary_name LIKE '% FARM'";
        specialCase += " OR beneficiary_name LIKE ' FAIR%' OR beneficiary_name LIKE '% FAIR'";
        whereClause.append(specialCase);
        
        String sql = "SELECT * FROM converted_data_beftn WHERE is_downloaded= ? and incentive != ? AND (" + whereClause.toString() + ")";
        Map<String, Object> params = new HashMap<>();
        params.put("1", 0);
        params.put("2", 0);
        return getData(sql,params);
    }

    public Object getBaseTableDataByTransactionNoAndFileInfoModelId(String entityName, int fileInfoModelId, String transactionNo){
        String sql = "SELECT n FROM " + entityName + " n WHERE n.transactionNo=:transactionNo and n.fileInfoModel.id=:fileInfoModelId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("transactionNo", transactionNo);
        query.setParameter("fileInfoModelId", fileInfoModelId);
        try{
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    @Transactional
    public Map<String, Object> deleteByTransactionNoAndFileInfoModelId(String entityName, int fileInfoModelId, String transactionNo){
        Map<String, Object> resp = CommonService.getResp(1, "Data not deleted", null);
        try{
            Query query = entityManager.createQuery("DELETE FROM " + entityName + " n WHERE n.transactionNo=:transactionNo AND n.fileInfoModel.id = :fileInfoModelId");
            query.setParameter("fileInfoModelId", fileInfoModelId);
            query.setParameter("transactionNo", transactionNo);
            int affectedRows = query.executeUpdate();
            resp = CommonService.getResp(0, "Data deleted successful", null);
            resp.put("affectedRows", affectedRows);
        }catch(Exception e){
            e.printStackTrace();
            return CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }

    public Map<String, Object> getIpRange(){
        Map<String, Object> resp = new HashMap<>();
        return resp;    
    }
        

}
