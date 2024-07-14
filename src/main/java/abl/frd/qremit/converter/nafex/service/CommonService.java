package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Service
public class CommonService {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static float incentivePercentage = 2.5f;
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
    
    public static <T> List<OnlineModel> generateOnlineModelList(List<T> models, String checkT24MethodName, String isProcessed) {
        List<OnlineModel> onlineList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkT24Method = singleModel.getClass().getMethod(checkT24MethodName);
                String checkT24Value = (String) checkT24Method.invoke(singleModel);
                if ("1".equals(checkT24Value)) {
                    onlineList.add(generateOnlineModel(singleModel,isProcessed));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return onlineList;
    }

    public static <T> List<OnlineModel> generateOnlineModelList(List<T> models, String checkT24MethodName){
        return generateOnlineModelList(models, checkT24MethodName,"0");
    }

    public static <T> OnlineModel generateOnlineModel(T model,String flag) {
        OnlineModel onlineModel = new OnlineModel();
        try {
            onlineModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            onlineModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            onlineModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            onlineModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            onlineModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            onlineModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
            onlineModel.setIsProcessed(flag);
            onlineModel.setIsDownloaded(flag);
            onlineModel.setDownloadDateTime(LocalDateTime.now());
            onlineModel.setDownloadUserId(9999);
            onlineModel.setExtraE("dump");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return onlineModel;
    }


    private static Object getPropertyValue(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        return method.invoke(obj);
    }

    public static <T> List<CocModel> generateCocModelList(List<T> models, String checkCocMethodName) {
        List<CocModel> cocList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkCocMethod = singleModel.getClass().getMethod(checkCocMethodName);
                String checkCocValue = (String) checkCocMethod.invoke(singleModel);
                if ("1".equals(checkCocValue)) {
                    cocList.add(generateCocModel(singleModel));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return cocList;
    }

    public static <T> CocModel generateCocModel(T model) {
        CocModel cocModel = new CocModel();
        try {
            cocModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            cocModel.setBankCode((String) getPropertyValue(model, "getBankCode"));
            cocModel.setBankName((String) getPropertyValue(model, "getBankName"));
            cocModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            cocModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            cocModel.setBranchCode((String) getPropertyValue(model, "getBranchCode"));
            cocModel.setBranchName((String) getPropertyValue(model, "getBranchName"));
            cocModel.setCocCode("15");
            cocModel.setCreditMark("CRED");
            cocModel.setCurrency((String) getPropertyValue(model, "getCurrency"));
            cocModel.setEnteredDate((String) getPropertyValue(model, "getEnteredDate"));
            cocModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            cocModel.setIsProcessed("0");
            cocModel.setIsDownloaded("0");
            cocModel.setDownloadDateTime(LocalDateTime.now());
            cocModel.setDownloadUserId(9999);
            cocModel.setExtraE("dummy");
            cocModel.setIncentive(00.00);
            cocModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            cocModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return cocModel;
    }
    public static <T> List<AccountPayeeModel> generateAccountPayeeModelList(List<T> models, String checkAccPayeeMethodName) {
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkAccPayeeMethod = singleModel.getClass().getMethod(checkAccPayeeMethodName);
                String checkAccPayeeValue = (String) checkAccPayeeMethod.invoke(singleModel);
                if ("1".equals(checkAccPayeeValue)) {
                    accountPayeeModelList.add(generateAccountPayeeModel(singleModel));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return accountPayeeModelList;
    }

    public static <T> AccountPayeeModel generateAccountPayeeModel(T model) {
        AccountPayeeModel accountPayeeModel = new AccountPayeeModel();
        try {
            accountPayeeModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            accountPayeeModel.setBankCode((String) getPropertyValue(model, "getBankCode"));
            accountPayeeModel.setBankName((String) getPropertyValue(model, "getBankName"));
            accountPayeeModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            accountPayeeModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            accountPayeeModel.setBranchCode((String) getPropertyValue(model, "getBranchCode"));
            accountPayeeModel.setBranchName((String) getPropertyValue(model, "getBranchName"));
            accountPayeeModel.setAccountPayeeCode("5");
            accountPayeeModel.setCreditMark("CRED");
            accountPayeeModel.setCurrency((String) getPropertyValue(model, "getCurrency"));
            accountPayeeModel.setEnteredDate((String) getPropertyValue(model, "getEnteredDate"));
            accountPayeeModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            accountPayeeModel.setIsProcessed("0");
            accountPayeeModel.setIsDownloaded("0");
            accountPayeeModel.setDownloadDateTime(LocalDateTime.now());
            accountPayeeModel.setDownloadUserId(9999);
            accountPayeeModel.setExtraE("dummy");
            accountPayeeModel.setIncentive(00.00);
            accountPayeeModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            accountPayeeModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return accountPayeeModel;
    }

    public static <T> List<BeftnModel> generateBeftnModelList(List<T> models, String checkBeftnMethodName) {
        List<BeftnModel> beftnModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkBeftnMethod = singleModel.getClass().getMethod(checkBeftnMethodName);
                String checkBeftnValue = (String) checkBeftnMethod.invoke(singleModel);
                if ("1".equals(checkBeftnValue)) {
                    beftnModelList.add(generateBeftnModel(singleModel));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return beftnModelList;
    }

    public static <T> BeftnModel generateBeftnModel(T model) {
        BeftnModel beftnModel = new BeftnModel();
        try {
            beftnModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            beftnModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            beftnModel.setBeneficiaryAccountType("SA");
            beftnModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            beftnModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            beftnModel.setIsProcessedMain("0");
            beftnModel.setIsProcessedIncentive("0");
            beftnModel.setIsIncDownloaded("0");
            beftnModel.setDownloadUserId(9999);
            beftnModel.setDownloadDateTime(LocalDateTime.now());
            beftnModel.setIncentive(calculatePercentage((Double) getPropertyValue(model, "getAmount")));
            beftnModel.setOrgAccountNo("160954");
            beftnModel.setOrgAccountType("CA");
            beftnModel.setOrgCustomerNo("7892");
            beftnModel.setOrgName("FRD Remittance");
            beftnModel.setRoutingNo((String) getPropertyValue(model, "getBranchCode"));
            beftnModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return beftnModel;
    }
    public static Double calculatePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (incentivePercentage / 100f) * mainAmount;
        return Double.valueOf(df.format(percentage));
    }
    public static String putCocFlag(String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return "0";
        }
        else if(accountNumber.contains("coc") || accountNumber.contains("COC")){
            return "1";
        }
        else {
            return "0";
        }
    }
    public static boolean isCocFound(String accountNumber){
        if(accountNumber.toLowerCase().contains("coc")){
            return true;
        }
        else {
            return false;
        }
    }
    public static String getOnlineAccountNumber(String accountNumber){
        //^.*02000(\d{8})$.*
        Pattern p = Pattern.compile("^.*02000(\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        String onlineAccountNumber=null;
        if (m.find())
        {
            onlineAccountNumber = m.group(1);
        }
        return onlineAccountNumber;
    }
    public static String putOnlineFlag(String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }
    public static boolean isOnlineAccoutNumberFound(String accountNumber){
        Pattern p = Pattern.compile("^.*02000(\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        if (m.find())
        {
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean isBeftnFound(String bankName, String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return false;
        }
        else if(isCocFound(accountNumber)){
            return false;
        }
        else if(bankName.toLowerCase().contains("agrani") || bankName.toLowerCase().contains("abl")) {
            return false;
        }
        else{
            return true;
        }
    }


    public static String putBeftnFlag(String bankName, String accountNumber){
        if(isBeftnFound(bankName, accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }
    public static boolean isAccountPayeeFound(String bankName, String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return false;
        }
        else if(isCocFound(accountNumber)) {
            return false;
        }
        else if(isBeftnFound(bankName,accountNumber)){
            return false;
        }
        else{
            return true;
        }
    }
    public static String putAccountPayeeFlag(String bankName, String accountNumber){
        if(isAccountPayeeFound(bankName, accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }

}
