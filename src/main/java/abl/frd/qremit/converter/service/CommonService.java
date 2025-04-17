package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.controller.ReportController;
import abl.frd.qremit.converter.model.*;
import abl.frd.qremit.converter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@SuppressWarnings("unchecked")
@Service
public class CommonService {
    @Value("${govt.incentive.percentage}")
    private float govtIncentivePercentage;
    private static float govtIncentivePercentageStatic;
    @Value("${agrani.incentive.percentage}")
    private float agraniIncentivePercentage;
    private static float agraniIncentivePercentageStatic;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    //private static float incentivePercentage = 2.5f;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    CustomQueryService customQueryService;

    public static String uploadSuccesPage = "pages/user/userUploadSuccessPage";
    //public static String dirPrefix = "../";
    @Value("${app.dir-prefix}")
    private String dirPrefix;
    public static String reportDir = "report/";

    @PostConstruct
    public void init() {
        govtIncentivePercentageStatic = govtIncentivePercentage;
        agraniIncentivePercentageStatic = agraniIncentivePercentage;
    }

    public Path generateOutputFile(String file) throws IOException{
        return generateOutputFile(reportDir, file);
    }
    public Path generateOutputFile(String dir, String file) throws IOException{
        Path reportPath = Paths.get(dirPrefix, dir);
        if (!Files.exists(reportPath)) {
            Files.createDirectories(reportPath);
        }
        Path outputPath = reportPath.resolve(file);
        return outputPath;
    }

    public Path getReportFile(String file) throws IOException{
        return generateOutputFile(reportDir + "daily_report/", file);
    }
    
    public static boolean hasCSVFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equalsIgnoreCase("text/csv") ||
                        contentType.equalsIgnoreCase("application/csv") ||
                        contentType.equalsIgnoreCase("application/vnd.ms-excel") ||
                        contentType.equalsIgnoreCase("text/plain") ||
                        contentType.equalsIgnoreCase("application/vnd.oasis.opendocument.spreadsheet")||
                        contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
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

    public static Map<String, Object> getResp(int err, String msg, List<Map<String, Object>> data) {
		Map<String, Object> resp = new HashMap<>();
		resp.put("err",err);
		resp.put("msg", msg);
		resp.put("data", data);
		return resp;
	}

    // Helper method to create columns dynamically from arrays
    public static List<Map<String, String>> createColumns(String[] columnData, String[] columnTitles) {
        List<Map<String, String>> columns = new ArrayList<>();
        for (int i = 0; i < columnData.length; i++) {
            columns.add(createColumn(columnData[i], columnTitles[i]));
        }
        return columns;
    }
        
    // Helper method to create a column map for table
    private static Map<String, String> createColumn(String data, String title) {
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
        resp.put("amount", generateClassForText(totalAmount, "fw-bold"));
        resp.put(totalDetails,  generateClassForText("Total","fw-bold"));
        return resp;
    }

    public static <T> List<OnlineModel> generateOnlineModelList(List<T> models, LocalDateTime uploadDateTime, int isProcessed){
        List<OnlineModel> onlineList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                String typeFlag = (String) getPropertyValue(singleModel, "getTypeFlag");
                if(("1").equals(typeFlag))  onlineList.add(generateOnlineModel(singleModel, uploadDateTime, isProcessed));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return onlineList;
    }

    public static <T> OnlineModel generateOnlineModel(T model, LocalDateTime uploadDateTime, int flag) {
        OnlineModel onlineModel = new OnlineModel();
        try {
            onlineModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            onlineModel.setGovtIncentive(calculateGovtIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            onlineModel.setAgraniIncentive(calculateAgraniIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            onlineModel.setIncentive(onlineModel.getGovtIncentive()+onlineModel.getAgraniIncentive());
            onlineModel.setBeneficiaryAccount((String) getPropertyValue(model, "getBeneficiaryAccount"));
            onlineModel.setBeneficiaryName((String) getPropertyValue(model, "getBeneficiaryName"));
            onlineModel.setExchangeCode((String) getPropertyValue(model, "getExchangeCode"));
            onlineModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            onlineModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
            onlineModel.setBankCode((String) getPropertyValue(model, "getBankCode"));
            onlineModel.setBankName((String) getPropertyValue(model, "getBankName"));
            onlineModel.setBranchCode((String) getPropertyValue(model, "getBranchCode"));
            onlineModel.setBranchName((String) getPropertyValue(model, "getBranchName"));
            onlineModel.setEnteredDate((String) getPropertyValue(model, "getEnteredDate"));
            onlineModel.setIsProcessed(flag);
            onlineModel.setIsDownloaded(flag);
            if(flag == 1){
                onlineModel.setDownloadDateTime(uploadDateTime);
            }
            onlineModel.setDownloadUserId(9999);
            onlineModel.setUploadDateTime(uploadDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return onlineModel;
    }

    public static Object getPropertyValue(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        return method.invoke(obj);
    }
    
    public static Object findDynamicMethodByParameter(Object obj, String methodName, Object params) throws Exception{
        Method method = obj.getClass().getMethod(methodName, params.getClass());
        return method.invoke(obj, params);
    }

    public static <T> List<CocModel> generateCocModelList(List<T> models, LocalDateTime uploadDateTime) {
        List<CocModel> cocList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                String typeFlag = (String) getPropertyValue(singleModel, "getTypeFlag");
                if(("4").equals(typeFlag))  cocList.add(generateCocModel(singleModel, uploadDateTime));
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return cocList;
    }

    public static <T> CocModel generateCocModel(T model, LocalDateTime uploadDateTime) {
        CocModel cocModel = new CocModel();
        try {
            cocModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            cocModel.setGovtIncentive(calculateGovtIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            cocModel.setAgraniIncentive(calculateAgraniIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            cocModel.setIncentive(cocModel.getGovtIncentive()+cocModel.getAgraniIncentive());
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
            cocModel.setDownloadUserId(9999);
            cocModel.setUploadDateTime(uploadDateTime);
            cocModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            cocModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return cocModel;
    }
    public static <T> List<AccountPayeeModel> generateAccountPayeeModelList(List<T> models, LocalDateTime uploadDateTime) {
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                String typeFlag = (String) getPropertyValue(singleModel, "getTypeFlag");
                if(("2").equals(typeFlag))  accountPayeeModelList.add(generateAccountPayeeModel(singleModel, uploadDateTime));
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }
        return accountPayeeModelList;
    }

    public static <T> AccountPayeeModel generateAccountPayeeModel(T model, LocalDateTime uploadDateTime) {
        AccountPayeeModel accountPayeeModel = new AccountPayeeModel();
        try {
            accountPayeeModel.setAmount((Double) getPropertyValue(model, "getAmount"));
            accountPayeeModel.setGovtIncentive(calculateGovtIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            accountPayeeModel.setAgraniIncentive(calculateAgraniIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            accountPayeeModel.setIncentive(accountPayeeModel.getGovtIncentive()+accountPayeeModel.getAgraniIncentive());
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
            accountPayeeModel.setDownloadUserId(9999);
            accountPayeeModel.setUploadDateTime(uploadDateTime);
            accountPayeeModel.setRemitterName((String) getPropertyValue(model, "getRemitterName"));
            accountPayeeModel.setTransactionNo((String) getPropertyValue(model, "getTransactionNo"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return accountPayeeModel;
    }

    public static <T> List<BeftnModel> generateBeftnModelList(List<T> models, LocalDateTime uploadDateTime) {
        List<BeftnModel> beftnModelList = new ArrayList<>();
        for (T singleModel : models) {
            try {
                String typeFlag = (String) getPropertyValue(singleModel, "getTypeFlag");
                if(("3").equals(typeFlag))  beftnModelList.add(generateBeftnModel(singleModel, uploadDateTime));
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
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
            beftnModel.setGovtIncentive(calculateGovtIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            beftnModel.setAgraniIncentive(calculateAgraniIncentivePercentage((Double) getPropertyValue(model, "getAmount")));
            beftnModel.setIncentive(beftnModel.getGovtIncentive()+beftnModel.getAgraniIncentive());
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
            beftnModel.setEnteredDate((String) getPropertyValue(model, "getEnteredDate"));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return beftnModel;
    }
    public static Double calculateGovtIncentivePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (govtIncentivePercentageStatic / 100f) * mainAmount;
        return Double.valueOf(df.format(percentage));
    }
    public static Double calculateAgraniIncentivePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (agraniIncentivePercentageStatic / 100f) * mainAmount;
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
        if(!checkAgraniBankName(bankName)){
            return true; //if not ABL it will be beftn
        }
        if(routingNo.isEmpty() || routingNo.length() != 9 || checkAgraniRoutingNo(routingNo)){
            return false;
        }
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

    public static Double convertStringToDouble(String str){
        Double number;
        try{
            number = Double.parseDouble(str);
        }catch(NumberFormatException e){
            number = 0.0;
        }
        return number;
    }

    public static String convertDoubleToString(Double number){
        return String.valueOf(number);
    }

    public static boolean checkEmptyString(String str){
        if(str == null || str.trim().isEmpty()) return true;
        try{
            //check number
            double number = Double.parseDouble(str);
            return number == 0.0;
        }catch(NumberFormatException e){
            return false;
        }
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

    public static boolean checkAblIslamiBankingWindow(String accountNo){
        if(accountNo.startsWith("60") && accountNo.length() == 13){
            int prefix = convertStringToInt(accountNo.substring(0,4));
            if(prefix >= 6001 && prefix <= 6099)    return true;
        }
        return false;
    }

    public static boolean checkAccountToBeOpened(String accountNo){
        if(accountNo.equalsIgnoreCase("Account to be opened"))  return true;
        return false;
    }

    public static String setTypeFlag(String benificiaryAccount, String bankName, String branchCode){
        String typeFlag = "0";
        String onlineFlag = putOnlineFlag(benificiaryAccount, bankName);
        String cocFlag = putCocFlag(benificiaryAccount);
        String accountPayeeFlag = putAccountPayeeFlag(bankName, benificiaryAccount, branchCode);
        String beftnFlag = putBeftnFlag(bankName, benificiaryAccount, branchCode);
        if(("1").equals(onlineFlag))   typeFlag = "1";
        else if(("1").equals(accountPayeeFlag))   typeFlag = "2";
        else if(("1").equals(beftnFlag))   typeFlag = "3";
        else if(("1").equals(cocFlag))   typeFlag = "4";
        return typeFlag;
    }

    public static String checkExchangeCode(String data, String exchangeCode, String nrtaCode){
        String errorMessage = "";
        //if(!data.equals(exchangeCode))  errorMessage = "Please Upload the Correct File";
        //if(!data.equals(nrtaCode))  errorMessage = "Please Upload the Correct File";  
        return errorMessage;
    }

    public Map<String,Object> convertAblRoutingToBranchCode(String branchCode, List<Map<String, Object>> routingData){
        Map<String,Object> data = new HashMap<>();
        if(branchCode.startsWith("010")){
            Map<String, Object> rdata = customQueryService.generateRoutingDetailsByRoutingNo(routingData, branchCode);
            if(!rdata.isEmpty()){
                data.put("branchCode", rdata.get("abl_branch_code").toString());
                data.put("branchName", rdata.get("branch_name").toString());
            }
        }
        return data;
    }

    public <T> Map<String, Object> generateFourConvertedDataModel(List<T> model, FileInfoModel fileInfoModel, User user, LocalDateTime currentDateTime, int isProcessed){
        Map<String, Object> resp = new HashMap<>();
        List<OnlineModel> onlineModelList = generateOnlineModelList(model, currentDateTime, isProcessed);
        List<CocModel> cocModelList = generateCocModelList(model, currentDateTime);
        List<AccountPayeeModel> accountPayeeModelList = generateAccountPayeeModelList(model, currentDateTime);
        List<BeftnModel> beftnModelList = generateBeftnModelList(model, currentDateTime);
        
        fileInfoModel.setCocModelList(cocModelList);
        fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
        fileInfoModel.setBeftnModelList(beftnModelList);
        fileInfoModel.setOnlineModelList(onlineModelList);
        Double fileTotalAmount = convertStringToDouble(fileInfoModel.getTotalAmount());
        Double totalAmount = (fileTotalAmount != null && fileTotalAmount != 0.0) ? fileTotalAmount: 0.0;

        List<Map<String, Object>> routingData = new ArrayList<>();
        if(!accountPayeeModelList.isEmpty() || !onlineModelList.isEmpty()){
            routingData = customQueryService.getRoutingDetailsByBankCode("010");
        }
        if(cocModelList != null){
            for (CocModel cocModel : cocModelList) {
                cocModel.setFileInfoModel(fileInfoModel);
                cocModel.setUserModel(user);
                totalAmount += cocModel.getAmount();
            }
        }
        if(accountPayeeModelList != null){
            for (AccountPayeeModel accountPayeeModel : accountPayeeModelList) {
                accountPayeeModel.setFileInfoModel(fileInfoModel);
                accountPayeeModel.setUserModel(user);
                Map<String, Object> rdata = convertAblRoutingToBranchCode(accountPayeeModel.getBranchCode(), routingData);
                if(!rdata.isEmpty()){
                    accountPayeeModel.setBranchCode(rdata.get("branchCode").toString());
                    accountPayeeModel.setBranchName(rdata.get("branchName").toString());
                }
                totalAmount += accountPayeeModel.getAmount();
            }
        }
        if(beftnModelList != null){
            for (BeftnModel beftnModel : beftnModelList) {
                beftnModel.setFileInfoModel(fileInfoModel);
                beftnModel.setUserModel(user);
                totalAmount += beftnModel.getAmount();
            }
        }
        if(onlineModelList != null){
            for (OnlineModel onlineModel : onlineModelList) {
                onlineModel.setFileInfoModel(fileInfoModel);
                onlineModel.setUserModel(user);
                if(isProcessed == 1)    onlineModel.setIsApi(1); //isProcessed =1 is for Api data
                Map<String, Object> rdata = convertAblRoutingToBranchCode(onlineModel.getBranchCode(), routingData);
                if(!rdata.isEmpty() && isProcessed == 0){
                    onlineModel.setBranchCode(rdata.get("branchCode").toString());
                    onlineModel.setBranchName(rdata.get("branchName").toString());
                }
                
                totalAmount += onlineModel.getAmount();
            }
        }
        fileInfoModel.setTotalAmount(convertNumberFormat(totalAmount, 2));

        resp.put("fileInfoModel", fileInfoModel);
        resp.put("onlineModelList", onlineModelList);
        resp.put("cocModelList", cocModelList);
        resp.put("accountPayeeModelList", accountPayeeModelList);
        resp.put("beftnModelList", beftnModelList);
        return resp;
    }

    public static FileInfoModel countFourConvertedDataModel(Map<String, Object> data){
        FileInfoModel fileInfoModel = (FileInfoModel)  data.get("fileInfoModel");
        List<OnlineModel> onlineModelList = (List<OnlineModel>) data.get("onlineModelList");
        List<CocModel> cocModelList = (List<CocModel>) data.get("cocModelList");
        List<AccountPayeeModel> accountPayeeModelList = (List<AccountPayeeModel>) data.get("accountPayeeModelList");
        List<BeftnModel> beftnModelList = (List<BeftnModel>) data.get("beftnModelList");
        fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
        fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
        fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
        fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
        return fileInfoModel;
    }

    public static <T> T createDataModel(T model, Map<String, Object> data){
        for(Map.Entry<String, Object> entry: data.entrySet()){
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            try{
                Field field = model.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.getType().equals(String.class)){
                    field.set(model, (String) fieldValue);
                }
                if(field.getType().equals(Double.class)){
                    double doubleField;
                    try{
                        doubleField = Double.parseDouble((String) fieldValue);
                    }catch(Exception e){
                        doubleField = 0;
                    } 
                    field.set(model, doubleField);
                }
                if(field.getType().equals(Integer.class) || field.getType().equals(int.class)){
                    field.set(model, convertStringToInt((String) fieldValue));
                }
                if(field.getType().equals(LocalDateTime.class)){
                    LocalDateTime dateField = convertStringToDate(fieldValue.toString());
                    field.set(model, dateField);
                }
            }catch(NoSuchFieldException | IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return model;
    }

    public static Object addFileInfoModelAndUserInModelInstance(Object modelInstance, FileInfoModel fileInfoModel, User user) throws Exception{
        Field field = modelInstance.getClass().getDeclaredField("fileInfoModel");
        field.setAccessible(true);
        field.set(modelInstance, fileInfoModel);
        Field ufield = modelInstance.getClass().getDeclaredField("userModel");
        ufield.setAccessible(true);
        ufield.set(modelInstance, user);
        return modelInstance;
    }

    public static Map<String, Object> convertModelToObject(Object model){
        Map<String, Object> map = new HashMap<>();
        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true); // Allow access to private fields
                map.put(field.getName(), field.get(model)); // Add field name and value
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static <T> Map<String, Object> convertDataModelToObject(T model, String userKey) throws Exception{
        if(model == null) return getResp(1, "No data found following transaction id", null);
        FileInfoModel fileInfoModel = (FileInfoModel) getPropertyValue(model, "getFileInfoModel");
        User user = (User) getPropertyValue(model, "getUserModel");
        int fileInfoModelId = fileInfoModel.getId();
        int userId = user.getId();
        Map<String, Object> obj = convertModelToObject(model);
        obj.put("fileInfoModelId", fileInfoModelId);
        obj.put(userKey, userId);
        obj.remove("userModel");
        obj.remove("fileInfoModel");
        return obj;
    }

    public static List<Map<String, Object>> convertDynamicListToMap(List<?> dataList, Class<?> modelClass){
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Object data: dataList){
            if(modelClass.isInstance(data)){
                Map<String, Object> fieldMap = new HashMap<>();
                Field[] fields = modelClass.getDeclaredFields();
                for(Field field: fields){
                    try{
                        field.setAccessible(true);
                        Object value = field.get(data); 
                        fieldMap.put(field.getName(), value);
                    }catch(IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                resultList.add(fieldMap);
            }
        }
        return resultList;
    }

    public static void addErrorDataModelList(List<ErrorDataModel> errorDataModelList, Map<String, Object> data, String exchangeCode, String errorMessage, LocalDateTime currentDateTime, User user, FileInfoModel fileInfoModel){
        ErrorDataModel errorDataModel = getErrorDataModel(data, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
        errorDataModelList.add(errorDataModel);
    }
    
    public static ErrorDataModel getErrorDataModel(Map<String, Object> data, String exchangeCode, String errorMessage, LocalDateTime currentDateTime, User user, FileInfoModel fileInfoModel){
        ErrorDataModel errorDataModel = new ErrorDataModel();
        errorDataModel = createDataModel(errorDataModel, data);
        errorDataModel.setErrorMessage(errorMessage);
        errorDataModel.setUploadDateTime(currentDateTime);
        errorDataModel.setTypeFlag("0");
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
        StringBuilder templateContent = new StringBuilder();
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        try (InputStream inputStream = resource.getInputStream();
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                templateContent.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return templateContent.toString();
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

         // Handle the IPv6 localhost address (::1)
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            remoteAddr = "127.0.0.1";
        }

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

    public static String fixRoutingNo(String routingNo){
        if(!routingNo.isEmpty() && routingNo.length() == 8){
            routingNo = "0" + routingNo;
        }
        return routingNo;
    }

    public static String checkBEFTNRouting(String routingNo){
        String errorMessage = "";
        String msg = "Invalid Routing Number for BEFTN";
        if(routingNo.length() != 9 || checkAgraniRoutingNo(routingNo)){
            return msg;
        }
        String regex = "^[0-9]+$";
        if(routingNo.startsWith("00"))  return msg;
        if(!routingNo.matches(regex))    return msg;
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
        }else if(checkAgraniRoutingNo(routingNo) && accountNo.startsWith("02000") && accountNo.length() != 13){
            errorMessage = "Invalid ABL Online A/C Number which requires 13 digits";  //check routing no
        }else if(checkAgraniBankName(bankName) && accountNo.startsWith("02000") && accountNo.length() != 13){
            errorMessage = "Invalid ABL Online A/C Number which requires 13 digits"; //check agrani bankName 
        }
        return errorMessage;
    }
    
    //string starts with CO
    public static String checkCOString(String accountNo){
        String errorMessage = "";
        if(accountNo.toLowerCase().startsWith("co")){
            errorMessage = "Invalid COC A/C name";
        }
        return errorMessage;
    }
    //check validation error message ends
    //error checking
    public static <T> Map<String, Object> checkError(Map<String, Object> data, List<ErrorDataModel> errorDataModelList, String nrtaCode, FileInfoModel fileInfoModel, 
        User user, LocalDateTime currentDateTime, String userExCode, Optional<T> duplicateData, List<String> transactionList){
        //userExCode- csv file first column
        Map<String, Object> resp = new HashMap<>();
        resp.put("err", 0);
        String errorMessage = "";
        String beneficiaryName = data.get("beneficiaryName").toString();
        String amount = data.get("amount").toString();
        String beneficiaryAccount = data.get("beneficiaryAccount").toString();
        String bankName = data.get("bankName").toString();
        String branchCode = data.get("branchCode").toString();
        String exchangeCode = data.get("exchangeCode").toString();
        String transactionNo = data.get("transactionNo").toString(); 
        //check duplicate data exists in database
        if(duplicateData.isPresent()){  // Checking Duplicate Transaction No in this block
            return getResp(3, "Duplicate Reference No " + transactionNo + " Found <br>", null);
        }
        errorMessage = getErrorMessage(beneficiaryAccount, beneficiaryName, amount, bankName, branchCode);
        if(!errorMessage.isEmpty()){
            addErrorDataModelList(errorDataModelList, data, exchangeCode, errorMessage, currentDateTime, user, fileInfoModel);
            resp = getResp(1, errorMessage, null);
            resp.put("errorDataModelList", errorDataModelList);
            return resp;
        }
        //check duplicate data exists in csv data
        if(transactionList.contains(transactionNo)){
            return getResp(4, "Duplicate Reference No " + transactionNo + " Found <br>", null);
        }else{
            transactionList.add(transactionNo);
            resp.put("transactionList", transactionList);
            
        }
        return resp;
    }

    public static String getErrorMessage(String beneficiaryAccount, String beneficiaryName, String amount, String bankName, String branchCode){
        String errorMessage = "";
        //a/c no, benficiary name, amount empty or null check
        errorMessage = checkBeneficiaryNameOrAmountOrBeneficiaryAccount(beneficiaryAccount, beneficiaryName, amount);
        if(!errorMessage.isEmpty())  return errorMessage;
        if(isBeftnFound(bankName, beneficiaryAccount, branchCode)){
            errorMessage = validateBeftn(bankName, branchCode);
            if(!errorMessage.isEmpty())  return errorMessage;
        }else if(isCocFound(beneficiaryAccount)){
            errorMessage = checkCOCBankName(bankName);
            if(!errorMessage.isEmpty())  return errorMessage;
        }else if(isAccountPayeeFound(bankName, beneficiaryAccount, branchCode)){
            errorMessage = validateAccountPayee(beneficiaryAccount, beneficiaryName, amount, bankName, branchCode);
            if(!errorMessage.isEmpty())  return errorMessage;
        }else if(isOnlineAccoutNumberFound(beneficiaryAccount)){
            
        }
        return errorMessage;
    }

    public static String validateBeftn(String bankName, String branchCode){
        String errorMessage = "";
        if(checkEmptyString(bankName)){
            return "Bank Name is empty. Please correct it";
        }
        errorMessage = checkBEFTNRouting(branchCode);
        return errorMessage;
    }

    public static String validateAccountPayee(String beneficiaryAccount, String beneficiaryName, String amount, String bankName, String branchCode){
        String errorMessage = "";
        errorMessage = checkABLAccountAndRoutingNo(beneficiaryAccount, branchCode, bankName);
        if(!errorMessage.isEmpty())     return errorMessage;     
        errorMessage = checkCOString(beneficiaryAccount);
        if(!errorMessage.isEmpty())     return errorMessage;
        if(checkEmptyString(branchCode)){
            return "Branch Code can not be empty for A/C payee";
        }
        if(checkAblIslamiBankingWindow(beneficiaryAccount) || checkAccountToBeOpened(beneficiaryAccount)){
            //only processed for a/c payee
        }else   return "Legacy A/C won't be processed. Online A/C needed";
        return errorMessage;
    }
    //error message
    public static String setErrorMessage(String duplicateMessage, int duplicateCount, int totalCount){
        String errorMessage = "";
        if(!duplicateMessage.isEmpty())  errorMessage = duplicateMessage;
        if(totalCount == duplicateCount){
            //resp.put("errorMessage", "All Data From Your Selected File Already Exists!");
            errorMessage = "All Data From Your Selected File Already Exists!";
        }else if(duplicateCount >= 1) errorMessage = duplicateMessage;
        return errorMessage;
    }

    public static LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now(ZoneId.of("UTC+6"));
    }

    public static String getCurrentDate(){
        return getCurrentDate("ddMMyyyy");
    }
    public static String getCurrentDate(String format){
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+6"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public static String convertDateToString(LocalDateTime date){
        return convertDateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String convertDateToString(LocalDateTime date, String format){
        if(date == null)    return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = date.format(formatter);
        return formattedDateTime;
    }

    public static String convertLocalDateToString(LocalDate date){
        return convertLocalDateToString(date,"yyyy-MM-dd");
    }
    public static String convertLocalDateToString(LocalDate date, String format){
        if(date == null)    return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = date.format(formatter);
        return formattedDateTime;
    }

    public static LocalDateTime convertStringToDate(String date){
        return convertStringToDate(date,"yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime convertStringToDate(String date, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Handles milliseconds too
        try{
            return LocalDateTime.parse(date, formatter);
        }catch(DateTimeParseException e){
            try{
                return LocalDateTime.parse(date, isoFormatter);
            }catch(DateTimeParseException e2){
                e2.printStackTrace();
                return null;
            }
            
        }
        
    }

    public static LocalDate convertStringToLocalDate(String date, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try{
            return LocalDate.parse(date, formatter);
        }catch(DateTimeException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, LocalDateTime> getStartAndEndDateTime(String date){
        Map<String, LocalDateTime> dateTimeRange = new HashMap<>();
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        LocalDateTime startDateTime = convertStringToDate(startDate);
        LocalDateTime endDateTime = convertStringToDate(endDate);
        dateTimeRange.put("startDateTime", startDateTime);
        dateTimeRange.put("endDateTime", endDateTime);
        return dateTimeRange;
    }

    public static Map<String, Object> getRemittanceTypes(){
        Map<String, Object> resp = new HashMap<>();
        resp.put("1", "Online");
        resp.put("2", "Account Payee");
        resp.put("3", "BEFTN");
        resp.put("4", "COC");
        return resp;
    }

    public static Map<String, Object> getErrorType(){
        Map<String, Object> resp = new HashMap<>();
        resp.put("1", "benificiaryName,amount,beneficiaryAccount");
        resp.put("2", "");
        return resp;
    }

    public static Model viewUploadStatus(Map<String, Object> resp, Model model) throws JsonProcessingException{
        FileInfoModel fileInfoModelObject = (FileInfoModel) resp.get("fileInfoModel");
        if(resp.containsKey("errorMessage")){
            model.addAttribute("message", resp.get("errorMessage"));
        }
        if(fileInfoModelObject != null){
            model.addAttribute("fileInfo", fileInfoModelObject);
            model.addAttribute("beftnIncentive", 0);
            int errorCount = fileInfoModelObject.getErrorCount();
            int beftnCount = convertStringToInt(fileInfoModelObject.getBeftnCount());
            if(beftnCount > 0){
                model.addAttribute("beftnIncentive", 1);
            }
            if(errorCount >= 1){
            List<Map<String, String>> columns = ReportController.getReportColumn("3");
                ObjectMapper objectMapper = new ObjectMapper();
                String reportColumn = objectMapper.writeValueAsString(columns);
                model.addAttribute("reportColumn", reportColumn);
                model.addAttribute("errorData", fileInfoModelObject.getId());
            }
        }
        return model;
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
    
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private static void writeToFile(byte[] contentBytes, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            bos.write(contentBytes);
            bos.flush();
        } catch (IOException e) {
            System.err.println("Error writing data to file: " + e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> generateFile(ByteArrayInputStream contentStream, int count, String fileName) throws IOException{
        Map<String, Object> resp = new HashMap<>();
        byte[] contentBytes = readBytes(contentStream);
        String tempFilePath =  generateOutputFile(fileName).toString();
        try {
            // Write to file
            writeToFile(contentBytes, tempFilePath);
        }catch (IOException e) {
            e.printStackTrace();
            return getResp(1, e.getMessage(), null);
        }
        String url = "/getReportFile?fileName=" + fileName;
        resp.put("url", url);
        resp.put("count", count);
        return resp;
    }

    public static String generateDynamicFileName(String text, String ext){
        String formattedDate = getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss"));
        String fileName = text + formattedDate + ext;
        return fileName;
    }

    public static String convertUnderScore(String str){
        return str.replace("-", "_");
    }

    public String generateFileName(String text, String date, String ext){
        return text + date.replace("-", "_") + ext;
    }

    public static Map<String, Object> checkApiOrBeftnData(String bankCode, int type){
        Map<String, Object> resp = getResp(0, "", null);
        String msg = "You selected wrong file. Please select the correct file.";
        switch (type) {
            case 1: //for api
                if(!("11").equals(bankCode))  resp = getResp(1, msg, null);
                break;
            case 0: //for beftn
                if(!bankCode.isEmpty()) resp = getResp(1, msg, null);
                break;
            default:
                resp = getResp(1, "Invalid Type", null);
                break;
        }
        return resp;
    }

    public static Integer convertStringToInt(String str){
        if(str == null)    return 0;
        try{
            return Integer.parseInt(str);
        }catch(NumberFormatException e){
            return 0;
        }
    }

    public static String convertIntToString(int number){
        return String.valueOf(number);
    }

    public static Double convertIntToDouble(int number){
        return Double.valueOf(number);
    }

    public static String convertNumberFormat(Double number, int digit){
        String format = "%." + digit + "f";
        return String.format(format, number);
    }

    public static String generateClassForText(String text, String cls){
        return "<div class='" + cls +"'>" + text + "</div>";
    }

    public static Workbook getWorkbook(InputStream is) throws IOException {
        try {
            // WorkbookFactory automatically detects whether it's .xls or .xlsx
            return WorkbookFactory.create(is);
        } catch (Exception e) {
            throw new IOException("Failed to open the Excel file. Please ensure it's in .xls or .xlsx format.", e);
        }
    }

    public static String getCellValueAsString(Cell cell){
        String str = "";
        switch (cell.getCellType()){
            case STRING:
                str = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    str = cell.getDateCellValue().toString();
                } else {
                    str = String.valueOf(cell.getNumericCellValue());
                    //str = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                }
                break;
            case BOOLEAN:
                str = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }
        return str;
    }

    public static List<String[]> setUniqueIndexList(String transactionNo, String amount, String exchangeCode, List<String[]> data){
        data.add(new String[]{transactionNo, amount, exchangeCode});
        return data;
    }

    public static Map<String, Object> getDuplicateTransactionNo(String transactionNo, Map<String, Object> modelResp){
        Map<String, Object> resp = new HashMap<>();
        resp.put("isDuplicate", 0);
        if((Integer) modelResp.get("err") == 0){
            List<Map<String, Object>> data = (List<Map<String, Object>>) modelResp.get("data");
            if(data.isEmpty())  return resp;
            for(Map<String, Object> rdata: data){
                String transNo = rdata.get("transaction_no").toString();
                if(transactionNo.toLowerCase().equals(transNo.toLowerCase())){
                    resp.put("isDuplicate", 1);
                    break;
                }
            }
        }
        return resp;
    }

    public static Map<String, Object> getSerachType(String type){
        Map<String, Object> resp = new HashMap<>();
        resp.put("1", "Transaction No");
        if(!type.equals("2"))    resp.put("2", "Beneficiary Account No");
        return resp;
    }

    public static String serializeInfoToJson(Map<String, Object> info){
        String infoStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try{
            infoStr = objectMapper.writeValueAsString(info);
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
        return infoStr;
    }

    public static String[] beftnIncentiveNotProcessingKeywords(){
        String[] keywords = {
            " PHONE", " CENTER", " LTD", " BANK", "BANK ", " TELECOM", " TRADERS", " STORE", " CLOTH"," BROTHERS", " ENTERPRIZE", " ENTERPRI", " COSMETICS", " MOBILE", " TRAVELS", 
            " TOURS", " NETWORK", " FARM "," ASSETS", " ASSET", " SOLUTIONS", " FUND", " ELECTRON", " SECURITIES", " EQUIPMENT", " COMPENSATION", "DEATH ", " GALLERY", " HOUSE", "M/S ", " BANGLADESH", 
            " BD", " LIMITED", " OVERSEAS", " DAIRY", " COLLECTION", " RICE", " AGENCY", " TEXTILE", " VARAITY", " MEDICAL", " HALL", " PHARMA", " OPTICAL", "PRIZE", " FAIR ",
            " GENERAL", "GENERAL ", " HOSPITAL", "BITAN", " TRADING", " SONS", " Equipment", " WEDB", " MADRASA", " ACADEMY", " PHOTOSTAT", " MOSJID", " MART", " FURNITURE", " PURBACHAL", 
            "PURBACHAL ","PROBASHI", " PALLI", " GLOBAL", " EDUCATION", " BUSINESS", " CONSULTANCY", "WAGE ", " EARNER", " KALYAN", " TAHBIL", " ASULTANCY", " CORPORATE", " FOUNDATION"
        };
        return keywords;
    }

    public static Map<String, Object> checkBeftnEditForSpecialExchange(String exchangeCode, String type){
        Map<String,Object> resp = getResp(0, "", null);
        String[] exchangeCodeList = {"7010226","7010228","7010290","7010299","111111"};
        if(exchangeCode.equals("444444")){
            if(("3").equals(type))  return getResp(1, "BEFTN not allowed in swift", null);
        }
        for(String exCode: exchangeCodeList){
            if(exCode.equals(exchangeCode)){
                if(!("3").equals(type)) resp = getResp(1, "You are not allowed to change beftn to any other type for specific exchange code", null);
            }
        }
        return resp;
    }

    public static String getSidebarNameByUserid(int userId){
        String sidebar = "";
        switch(userId){
            case 0:
                sidebar = "sidebarAdmin";
                break;
            case 8888:
                sidebar = "sidebarSuperAdmin";
                break;
            default:
                sidebar = "sidebarUser";
                break;
        }
        return sidebar;
    }

    public static String removeAllSpecialCharacterFromString(String str){
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static <T> Map<String, Object> processDataToModel(List<Map<String, Object>> dataList, FileInfoModel fileInfoModel, User user, Map<String, Object> uniqueDataList, 
        Map<String, Object> archiveDataList, LocalDateTime currentDateTime, Optional<T> duplicateData, Class<T> modelClass, Map<String, Object> resp, List<ErrorDataModel> errorDataModelList, String fileExchangeCode, int checkType, int type){
        Map<String, Object> modelResp = new HashMap<>();
        List<String> transactionList = new ArrayList<>();
        String duplicateMessage = "";
        int duplicateCount = 0;
        List<T> modelList = new ArrayList<>();
        int isValidFile = 0;
        for(Map<String, Object> data: dataList){
            String transactionNo = data.get("transactionNo").toString();
            String exchangeCode = data.get("exchangeCode").toString();
            String nrtaCode = data.get("nrtaCode").toString();
            String bankName = data.get("bankName").toString();
            if(fileExchangeCode.equals(""))    fileExchangeCode = nrtaCode;
            //check exchange code
            if(isValidFile == 0){
                String exchangeMessage = checkExchangeCode(fileExchangeCode, exchangeCode, nrtaCode);
                if(!exchangeMessage.isEmpty()){
                    resp.put("errorMessage", exchangeMessage);
                    break;
                }else isValidFile = 1;
            }
            
            String beneficiaryAccount = data.get("beneficiaryAccount").toString();
            String branchCode = data.get("branchCode").toString();
            data.remove("nrtaCode");
            Map<String, Object> dupResp = getDuplicateTransactionNo(transactionNo, uniqueDataList);
            if((Integer) dupResp.get("isDuplicate") == 1){
                duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                duplicateCount++;
                continue;
            }
            Map<String, Object> archiveResp = getDuplicateTransactionNo(transactionNo, archiveDataList);
            if((Integer) archiveResp.get("isDuplicate") == 1){
                duplicateMessage +=  "Duplicate Reference No " + transactionNo + " Found <br>";
                duplicateCount++;
                continue;
            }
        
            Map<String, Object> errResp = checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, fileExchangeCode, duplicateData, transactionList);
            if((Integer) errResp.get("err") == 1){
                errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                continue;
            }
            if((Integer) errResp.get("err") == 4){
                duplicateMessage += errResp.get("msg");
                continue;
            }
            if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");
            String typeFlag = setTypeFlag(beneficiaryAccount, bankName, branchCode);
            /*
             * need to modify
             */
            if(checkType == 1){
                int allowedType = (type == 1) ? 1:3;  //for betn 3
                if(!convertStringToInt(typeFlag).equals(allowedType)){
                    String msg = "Invalid Remittence Type for ";
                    msg += (type == 1) ? "API": "BEFTN";
                    addErrorDataModelList(errorDataModelList, data, exchangeCode, msg, currentDateTime, user, fileInfoModel);
                    continue;
                }
            }
            try{
                T modelInstance = modelClass.getDeclaredConstructor().newInstance();
                modelInstance = createDataModel(modelInstance, data);
                Method setTypeFlagMethod = modelClass.getMethod("setTypeFlag", String.class);
                setTypeFlagMethod.invoke(modelInstance, typeFlag);
                Method setUploadDateTimeMethod = modelClass.getMethod("setUploadDateTime", LocalDateTime.class);
                setUploadDateTimeMethod.invoke(modelInstance, currentDateTime);
                modelList.add(modelInstance);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        modelResp.put("errorDataModelList", errorDataModelList);
        modelResp.put("modelList",modelList);
        modelResp.put("resp",resp);
        modelResp.put("duplicateMessage", duplicateMessage);
        modelResp.put("duplicateCount", duplicateCount);
        modelResp.put("transactionList", transactionList);
        return modelResp;
    }
    
}
