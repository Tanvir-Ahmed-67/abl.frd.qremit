package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;

import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.nio.file.*;

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
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    public String uploadSuccesPage = "/pages/user/userUploadSuccessPage";
    public String uploadApiSuccessPage = "/pages/user/userApiUploadSuccessPage";
    private final EntityManager entityManager;
    private final DataSource dataSource;

    public CommonService(EntityManager entityManager,DataSource dataSource){
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }
    public static boolean hasCSVFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equalsIgnoreCase("text/csv") ||
                        contentType.equalsIgnoreCase("application/csv") ||
                        contentType.equalsIgnoreCase("application/vnd.ms-excel") ||
                        contentType.equalsIgnoreCase("text/plain") ||
                        contentType.equalsIgnoreCase("application/vnd.oasis.opendocument.spreadsheet"));
    }
    public boolean ifFileExist(String fileName){
        if (fileInfoModelRepository.findByFileName(fileName) != null) {
            return true;
        }
        return false;
    }
    public List<Integer> CountAllFourTypesOfData(){
        List<Integer> count = new ArrayList<Integer>(5);
        count.add(onlineModelRepository.countByIsProcessed(0));
        //count.add(cocModelRepository.countByIsProcessed(0));
        count.add(cocModelRepository.countByIsDownloaded(0));
        count.add(accountPayeeModelRepository.countByIsProcessed(0));
        count.add(beftnModelRepository.countByIsProcessedMain(0));
        count.add(beftnModelRepository.countByIsProcessedIncentive(0));
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
    
    public static <T> List<OnlineModel> generateOnlineModelList(List<T> models, String checkT24MethodName, String isProcessed, LocalDateTime uploadDateTime) {
        List<OnlineModel> onlineList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkT24Method = singleModel.getClass().getMethod(checkT24MethodName);
                String checkT24Value = (String) checkT24Method.invoke(singleModel);
                if ("1".equals(checkT24Value)) {
                    onlineList.add(generateOnlineModel(singleModel,isProcessed, uploadDateTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return onlineList;
    }

    public static <T> List<OnlineModel> generatOnlineModelListFromErrorData(Object object, String checkT24MethodName, String isProcessed, LocalDateTime uploadDateTime){
        List<OnlineModel> onlineList = new ArrayList<>();
        try{
            Method checkT24Method = object.getClass().getMethod(checkT24MethodName);
            String checkT24Value = (String) checkT24Method.invoke(object);
            if ("1".equals(checkT24Value)) {
                onlineList.add(generateOnlineModel(object, isProcessed, uploadDateTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return onlineList;
    }    

    public static <T> List<OnlineModel> generateOnlineModelList(List<T> models, String checkT24MethodName, LocalDateTime uploadDateTime){
        return generateOnlineModelList(models, checkT24MethodName,"0", uploadDateTime);
    }

    public static <T> OnlineModel generateOnlineModel(T model,String flag, LocalDateTime uploadDateTime) {
        OnlineModel onlineModel = new OnlineModel();
        int flagInt = Integer.parseInt(flag);
        try {
            onlineModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            onlineModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            onlineModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            onlineModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            onlineModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            onlineModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
            onlineModel.setBankCode((String) getPropertyValue(model, "getBankCode"));
            onlineModel.setBankName((String) getPropertyValue(model, "getBankName"));
            onlineModel.setBranchCode((String) getPropertyValue(model, "getBranchCode"));
            onlineModel.setBranchName((String) getPropertyValue(model, "getBranchName"));
            onlineModel.setIsProcessed(flagInt);
            onlineModel.setIsDownloaded(flagInt);
            onlineModel.setDownloadDateTime(LocalDateTime.now());
            onlineModel.setDownloadUserId(9999);
            onlineModel.setUploadDateTime(uploadDateTime);
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

    public static <T> List<CocModel> generateCocModelList(List<T> models, String checkCocMethodName, LocalDateTime uploadDateTime) {
        List<CocModel> cocList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkCocMethod = singleModel.getClass().getMethod(checkCocMethodName);
                String checkCocValue = (String) checkCocMethod.invoke(singleModel);
                if ("1".equals(checkCocValue)) {
                    cocList.add(generateCocModel(singleModel, uploadDateTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return cocList;
    }

    public static <T> List<CocModel> generatCocModelListFromErrorData(Object object, String checkCocMethodName, LocalDateTime uploadDateTime){
        List<CocModel> cocList = new ArrayList<>();
        try{
            Method checkCocMethod = object.getClass().getMethod(checkCocMethodName);
            String checkCocValue = (String) checkCocMethod.invoke(object);
            if ("1".equals(checkCocValue)) {
               cocList.add(generateCocModel(object, uploadDateTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return cocList;
    }

    public static <T> CocModel generateCocModel(T model, LocalDateTime uploadDateTime) {
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
            cocModel.setDownloadDateTime(LocalDateTime.now());
            cocModel.setDownloadUserId(9999);
            cocModel.setUploadDateTime(uploadDateTime);
            cocModel.setIncentive(00.00);
            cocModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            cocModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return cocModel;
    }
    public static <T> List<AccountPayeeModel> generateAccountPayeeModelList(List<T> models, String checkAccPayeeMethodName, LocalDateTime uploadDateTime) {
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkAccPayeeMethod = singleModel.getClass().getMethod(checkAccPayeeMethodName);
                String checkAccPayeeValue = (String) checkAccPayeeMethod.invoke(singleModel);
                if ("1".equals(checkAccPayeeValue)) {
                    accountPayeeModelList.add(generateAccountPayeeModel(singleModel, uploadDateTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return accountPayeeModelList;
    }

    public static <T> List<AccountPayeeModel> generatAccountPayeeModelListFromErrorData(Object object, String checkAccPayeeMethodName, LocalDateTime uploadDateTime){
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        try{
            Method checkAccPayeeMethod = object.getClass().getMethod(checkAccPayeeMethodName);
            String checkAccPayeeValue = (String) checkAccPayeeMethod.invoke(object);
            if ("1".equals(checkAccPayeeValue)) {
               accountPayeeModelList.add(generateAccountPayeeModel(object, uploadDateTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return accountPayeeModelList;
    }

    public static <T> AccountPayeeModel generateAccountPayeeModel(T model, LocalDateTime uploadDateTime) {
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
            accountPayeeModel.setDownloadDateTime(LocalDateTime.now());
            accountPayeeModel.setDownloadUserId(9999);
            accountPayeeModel.setUploadDateTime(uploadDateTime);
            accountPayeeModel.setIncentive(00.00);
            accountPayeeModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            accountPayeeModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return accountPayeeModel;
    }

    public static <T> List<BeftnModel> generateBeftnModelList(List<T> models, String checkBeftnMethodName, LocalDateTime uploadDateTime) {
        List<BeftnModel> beftnModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                Method checkBeftnMethod = singleModel.getClass().getMethod(checkBeftnMethodName);
                String checkBeftnValue = (String) checkBeftnMethod.invoke(singleModel);
                if ("1".equals(checkBeftnValue)) {
                    beftnModelList.add(generateBeftnModel(singleModel, uploadDateTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return beftnModelList;
    }

    public static <T> List<BeftnModel> generateBeftnModelListFromErrorData(Object object, String checkBeftnMethodName, LocalDateTime uploadDateTime){
        List<BeftnModel> beftnModelList = new ArrayList<>();
        try{
            Method checkBeftnMethod = object.getClass().getMethod(checkBeftnMethodName);
            String checkBeftnValue = (String) checkBeftnMethod.invoke(object);
            if ("1".equals(checkBeftnValue)) {
                beftnModelList.add(generateBeftnModel(object, uploadDateTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return beftnModelList;
    }

    public static <T> BeftnModel generateBeftnModel(T model, LocalDateTime uploadDateTime) {
        BeftnModel beftnModel = new BeftnModel();
        try {
            beftnModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            beftnModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            beftnModel.setBeneficiaryAccountType("SA");
            beftnModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            beftnModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            beftnModel.setDownloadUserId(9999);
            beftnModel.setDownloadDateTime(LocalDateTime.now());
            beftnModel.setIncentive(calculatePercentage((Double) getPropertyValue(model, "getAmount")));
            beftnModel.setOrgAccountNo("160954");
            beftnModel.setOrgAccountType("CA");
            beftnModel.setOrgCustomerNo("7892");
            beftnModel.setOrgName("FRD Remittance");
            beftnModel.setUploadDateTime(uploadDateTime);
            beftnModel.setRoutingNo((String) getPropertyValue(model, "getBranchCode"));
            beftnModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
            beftnModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            beftnModel.setBankName((String) getPropertyValue(model, "getBankName"));
            beftnModel.setBankCode((String) getPropertyValue(model, "getBankCode"));
            beftnModel.setBranchName((String) getPropertyValue(model, "getBranchName"));
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
    public static String putOnlineFlag(String accountNumber, String bankName){
        if(isOnlineAccoutNumberFound(accountNumber, bankName)){
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
        
        return false;
    }

    public static boolean isOnlineAccoutNumberFound(String accountNumber, String bankName){
        Pattern p = Pattern.compile("^.*02000(\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        if(bankName.toLowerCase().contains("agrani") || bankName.toLowerCase().contains("abl")){
            if (m.find())
            {
                return true;
            }else return false;
        }
        return false;
    }
    public static boolean isBeftnFound(String bankName, String accountNumber, String routingNo){
        if(routingNo.isEmpty() || routingNo.length() != 9 || checkAgraniRoutingNo(routingNo)){
            return false;
        }
        //if(routingNo.matches("^010.*"))  return false;
        /* 
        if(!(routingNo.matches("^010.*") && routingNo.length() == 9)){
            return true;
        }
        else if(!(routingNo.matches("^10.*") && routingNo.length() == 8)){
            return true;
        }
        */
        else if(isOnlineAccoutNumberFound(accountNumber, bankName)){
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

    public static boolean isBeftnFound(String bankName, String accountNumber){
        return isBeftnFound(bankName, accountNumber,"");
    }    

    public static String putBeftnFlag(String bankName, String accountNumber, String routingNo){
        if(isBeftnFound(bankName, accountNumber,routingNo)){
            return "1";
        }
        else{
            return "0";
        }
    }
    public static boolean isAccountPayeeFound(String bankName, String accountNumber, String routingNo){
        if(isOnlineAccoutNumberFound(accountNumber, bankName)){
            return false;
        }
        else if(isCocFound(accountNumber)) {
            return false;
        }
        else if(isBeftnFound(bankName,accountNumber,routingNo)){
            return false;
        }
        else{
            return true;
        }
    }
    public static String putAccountPayeeFlag(String bankName, String accountNumber, String routingNo){
        if(isAccountPayeeFound(bankName, accountNumber, routingNo)){
            return "1";
        }
        else{
            return "0";
        }
    }

    public static boolean checkEmptyString(String str){
        if(str == null || str.trim().isEmpty() || str.trim().equals("0.0")) return true;
        else return false;
    }

    public static boolean checkAgraniRoutingNo(String routingNo){
        if(routingNo.length() == 9  && routingNo.matches("^010.*")){
            return true;
        }
        return false;
    }

    public static boolean checkAgraniBankName(String bankName){
        if(bankName.toLowerCase().contains("agrani") || bankName.toLowerCase().contains("abl")) return true;
        return false;
    }

    public static void addErrorDataModelList(List<ErrorDataModel> errorDataModelList, CSVRecord csvRecord, String exchangeCode, String errorMessage, LocalDateTime currentDateTime, User user, FileInfoModel fileInfoModel){
        ErrorDataModel errorDataModel = getErrorDataModel(csvRecord, exchangeCode, errorMessage, "0", "0", "0", "0", currentDateTime, user, fileInfoModel);
        errorDataModelList.add(errorDataModel);
    }
    
    public static ErrorDataModel getErrorDataModel(CSVRecord csvRecord, String exchangeCode, String errorMessage, String checkT24, String checkCoc, String checkAccPayee, String checkBEFTN, LocalDateTime currentDateTime, User user, FileInfoModel fileInfoModel){
        double amount;
        try{
            amount = Double.parseDouble(csvRecord.get(3));
        }catch(Exception e){
            amount = 0;
        }
        ErrorDataModel errorDataModel = new ErrorDataModel(
            exchangeCode, //exCode
            csvRecord.get(1), //Tranno
            csvRecord.get(2), //Currency
            amount, //Amount
            csvRecord.get(4), //enteredDate
            csvRecord.get(5), //remitter
            csvRecord.get(17), //remitterMobile
            csvRecord.get(6), // beneficiary
            csvRecord.get(7), //beneficiaryAccount
            csvRecord.get(12), //beneficiaryMobile
            csvRecord.get(8), //bankName
            csvRecord.get(9), //bankCode
            csvRecord.get(10), //branchName
            csvRecord.get(11), // branchCode
            csvRecord.get(13), //draweeBranchName
            csvRecord.get(14), //draweeBranchCode
            csvRecord.get(15), //purposeOfRemittance
            csvRecord.get(16), //sourceOfIncome
            "Not Processed",    // processed_flag
            "type",             // type_flag
            "processedBy",      // Processed_by
            "dummy",            // processed_date
            errorMessage, //error_message
            currentDateTime, //error_generation_date
            checkT24, // checkT24
            checkCoc,  //checkCoc
            checkAccPayee, //checkAccPayee
            checkBEFTN, // Checking Beftn
            0
        );
        errorDataModel.setUserModel(user);
        errorDataModel.setFileInfoModel(fileInfoModel);
        return errorDataModel;
    }

    //generate template from file 
    public static String getProcessedTemplate(String templateName, Map<String, String> variables) {
        String template = readTemplateFromFile(templateName);
        return replaceVariables(template, variables);
    }

    private static String readTemplateFromFile(String templateName) {
        try {
            Path path = Paths.get(ResourceUtils.getFile("classpath:templates/" + templateName).toURI());
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String replaceVariables(String template, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("{{ " + entry.getKey() + " }}", entry.getValue());
        }
        return template;
    }

    public static String generateTemplateBtn(String file,String url, String cls, String id, String title){
        Map<String, String> params = new HashMap<>();
        params.put("class", cls);
        params.put("url", url);
        params.put("id",id);
        params.put("title", title);
        return getProcessedTemplate(file, params);
    }

    public static Map<String, String> getNrtaCodeVsExchangeCodeMap(List<ExchangeHouseModel> exchangeHouseModelList){
        Map<String, String> nrtaCodeVsExchangeCodeMap = new HashMap<>();
        String exchangeCode;
        String nrtaCode;
        for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
            try{
                if(exchangeHouseModel.getExchangeCode().equals("710000") && exchangeHouseModel.getExchangeCode().equals("720000")){
                    continue;
                }
                nrtaCode = exchangeHouseModel.getNrtaCode();
                exchangeCode = exchangeHouseModel.getExchangeCode();
                nrtaCodeVsExchangeCodeMap.put(nrtaCode, exchangeCode);
            }catch (Exception e) {
                throw new RuntimeException("Failed to map NRTA Code Vs Exchange Code: " + e.getMessage());
            }
        }
        return nrtaCodeVsExchangeCodeMap;
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // The X-Forwarded-For header can contain multiple IPs, the first one is the client IP
            return xForwardedFor.split(",")[0];
        }
        return remoteAddr;
    }

    public static String getBaseTableName(String baseTableName){
        String tbl = "base_data_table_" + baseTableName;
        return tbl;
    }

    //check validation error message starts
    //a/c no, benficiary name, amount empty or null check
    public static String checkBeneficiaryNameOrAmountOrBeneficiaryAccount(String beneficiaryAccount, String beneficiaryName, String amount){
        String errorMessage = "";
        if(checkEmptyString(beneficiaryName) || checkEmptyString(beneficiaryAccount) || checkEmptyString(amount)){
            errorMessage = "A/C Number or Beneficiary Name or Amount can not be empty";
        }
        return errorMessage;
    }

    public static String checkBEFTNRouting(String routingNo){
        String errorMessage = "";
        if(routingNo.length() != 9 || checkAgraniRoutingNo(routingNo)){
            errorMessage = "Invalid Routing Number for BEFTN";
        }
        return errorMessage;
    }

    public static String checkCOCBankName(String bankName){
        String errorMessage = "";
        if(!checkAgraniBankName(bankName)){
            errorMessage = "Invalid Bank Name";
        }
        return errorMessage;
    }

    //check ABL A/C starts with 02** and routing no is not matched with ABL
    public static String checkABLAccountAndRoutingNo(String accountNo, String routingNo, String bankName){
        String errorMessage = "";
        if(isOnlineAccoutNumberFound(accountNo) && (!checkAgraniRoutingNo(routingNo) || !checkAgraniBankName(bankName))){
            errorMessage = "Invalid Routing Number or Bank Name";
        }else if(checkAgraniRoutingNo(routingNo) && accountNo.startsWith("02") && accountNo.length() != 13){
            errorMessage = "Invalid ABL Online A/C Number which requires 13 digits";  //check routing no
        }else if(checkAgraniBankName(bankName) && accountNo.startsWith("02") && accountNo.length() != 13){
            errorMessage = "Invalid ABL Online A/C Number which requires 13 digits"; //check agrani bankName 
        }
        return errorMessage;
    }
    
    //string starts with CO
    public static String checkCOString(String accountNo){
        String errorMessage = "";
        if(accountNo.toLowerCase().contains("co")){
            errorMessage = "Invalid COC A/C name";
        }
        return errorMessage;
    }
    //check validation error message ends
    public static String getCurrentDate(){
        return getCurrentDate("ddMMyyyy");
    }
    public static String getCurrentDate(String format){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public static String convertDateToString(LocalDateTime date){
        return convertDateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String convertDateToString(LocalDateTime date, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = date.format(formatter);
        return formattedDateTime;
    }

    public static LocalDateTime convertStringToDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return localDateTime;
    }

}
