package abl.frd.qremit.converter.nafex.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.ReportRepository;



@Service
public class ReportService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    ReportRepository reportRepository;

    private final EntityManager entityManager;
    private final DataSource dataSource;
    public ReportService(EntityManager entityManager,DataSource dataSource){
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }

    public ExchangeHouseModel findByExchangeCode(String exchangeCode){
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findByExchangeCode(exchangeCode);
        return exchangeHouseModel;
    }

    public Map<String,Object> getData(String sql){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        //Query query = entityManager.createNativeQuery(sql);
        try{
            Connection con = dataSource.getConnection();
            //PreparedStatement pstmt = createPreparedStatement(con, sql, params);
            System.out.println(sql);
           // ResultSet rs = pstmt.executeQuery(sql);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            if(!rs.next()){
                return getResp(1,"No data found",rows);
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
                return getResp(0, "Data Found", rows);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }



    public static Map<String, Object> getResp(int err, String msg, List<Map<String, Object>> data) {
		Map<String, Object> resp = new HashMap<>();
		resp.put("err",err);
		resp.put("msg", msg);
		resp.put("data", data);
		return resp;
	}

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        String queryStr = String.format("SELECT a.* FROM %s a, upload_file_info b WHERE a.file_info_model_id = b.id AND b.id = %s", tableName,fileInfoId);
        Map<String, Object> params = new HashMap<>();
       // params.put("fileInfoId", fileInfoId);
        //return reportRepository.getFileDetails(tableName, fileInfoId);
        Map<String,Object> resp = getData(queryStr);
        return resp;
    }



}
