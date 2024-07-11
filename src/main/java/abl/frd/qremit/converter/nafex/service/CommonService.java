package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Service
public class CommonService {
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    NafexModelRepository nafexModelRepository;
    @Autowired
    NafexModelRepository BecModelRepository;
    @Autowired
    NafexModelRepository MuzainiModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    public static String TYPE = "text/csv";
    public String uploadSuccesPage = "/pages/user/userUploadSuccessPage";
    private final EntityManager entityManager;
    private final DataSource dataSource;

    public CommonService(EntityManager entityManager,DataSource dataSource){
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }

    public static boolean hasCSVFormat(MultipartFile file) {
        if (TYPE.equals(file.getContentType())
                || file.getContentType().equals("text/plain")) {
            return true;
        }
        return false;
    }
    public boolean ifFileExist(String fileName){
        if (fileInfoModelRepository.findByFileName(fileName) != null) {
            return true;
        }
        return false;
    }
    public List<Integer> CountAllFourTypesOfData(){
        List<Integer> count = new ArrayList<Integer>(5);
        count.add(onlineModelRepository.countByIsProcessed("0"));
        count.add(cocModelRepository.countByIsProcessed("0"));
        count.add(accountPayeeModelRepository.countByIsProcessed("0"));
        count.add(beftnModelRepository.countByIsProcessedMain("0"));
        count.add(beftnModelRepository.countByIsProcessedIncentive("0"));
        return count;
    }

    public Map<String,Object> getData(String sql, Map<String, Object> params){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        try{
            Connection con = dataSource.getConnection();            
            PreparedStatement pstmt = con.prepareStatement(sql);
            int j = 1;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                pstmt.setObject(j++, entry.getValue());
            }
            
            ResultSet rs = pstmt.executeQuery();
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

    // Helper method to create columns dynamically from arrays
    public List<Map<String, String>> createColumns(String[] columnData, String[] columnTitles) {
        List<Map<String, String>> columns = new ArrayList<>();
        for (int i = 0; i < columnData.length; i++) {
            columns.add(createColumn(columnData[i], columnTitles[i]));
        }
        return columns;
    }
        
    // Helper method to create a column map for table
    private Map<String, String> createColumn(String data, String title) {
        Map<String, String> column = new HashMap<>();
        column.put("data", data);
        column.put("title", title);
        return column;
    }

    //generate totalAmount from table
    public Map<String,Object> getTotalAmountData(String[] columnData,double amount,String totalDetails){
        DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal palces
        String totalAmount = df.format(amount);
        Map<String, Object> resp = new HashMap<>();
        for(String cData: columnData){
            resp.put(cData, "");
        }
        resp.put("amount", totalAmount);
        resp.put(totalDetails,"Total");
        return resp;
    }
}
