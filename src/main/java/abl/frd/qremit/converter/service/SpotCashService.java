package abl.frd.qremit.converter.service;
import org.apache.commons.csv.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.SpotCashModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;

import java.io.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@SuppressWarnings("unchecked")
public class SpotCashService {
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
    @Autowired
    ApiService apiService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setUserModel(user);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            Map<String, Object> config = getExchangeConfig(exchangeCode);
            Map<String, Object> spotCashData = new HashMap<>();
            System.out.println(config);
            int excel = (int) config.get("excel");
            if(excel == 1){
                spotCashData = processExcelData(file.getInputStream(), user, fileInfoModel, currentDateTime, exchangeCode, nrtaCode, config);
            }else{
                spotCashData = processCsvData(file.getInputStream(), user, fileInfoModel, currentDateTime, exchangeCode, nrtaCode, config);
            }
        }catch(Exception e){
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
        return resp;
    }

    public Map<String, Object> processCsvData(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String exchangeCode, 
        String nrtaCode, Map<String, Object> config){
        Map<String, Object> resp = new HashMap<>();
        Optional<SpotCashModel> duplicateData = Optional.empty();
        List<Map<String, Object>> countryList = customQueryService.getCountryList();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            String methodName = (String) config.get("method");
            Method dynamicMethod = this.getClass().getDeclaredMethod(methodName, Iterable.class, String.class, String.class,List.class);
            dynamicMethod.setAccessible(true);
            Map<String, Object> dataResp = (Map<String, Object>) dynamicMethod.invoke(this, csvRecords, exchangeCode, nrtaCode, countryList);
            //System.out.println(dataResp);
        } catch (Exception e) {
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
        return resp;
    }

    public Map<String, Object> processExcelData(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String exchangeCode, 
        String nrtaCode, Map<String, Object> config){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> countryList = customQueryService.getCountryList();
        try{
            Workbook records = CommonService.getWorkbook(is);
            Sheet worksheet = records.getSheetAt(0);
            String methodName = (String) config.get("method");
            Method dynamicMethod = this.getClass().getDeclaredMethod(methodName, Sheet.class, String.class, String.class,List.class);
            dynamicMethod.setAccessible(true);
            Map<String, Object> dataResp = (Map<String, Object>) dynamicMethod.invoke(this, worksheet, exchangeCode, nrtaCode, countryList);
        }catch(Exception e){
            e.printStackTrace();
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
        return resp;
    }

    public Map<String, Object> getExchangeConfig(String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        int excel = 0;
        String method = "";
        switch(exchangeCode){
            case "7010226":
            case "7010228":
                method = "processApi";
                break;
            case "7010288":
                method = "processCbl";
                break;
            case "7010260":
                method = "processInstantCash";
                break;
            case "7010299":
                method = "processEzRemit";
                break;
            case "7010290":
                method = "processRia";
                break;
            case "7010307":
                method = "processWesternUnion";
                excel = 1;
                break;
            case "7010308":
                method = "processMoneyGram";
                excel = 1;
                break;
            case "7010229":
                method = "processNecItaly";
                excel = 1;
                break;
            case "7010272":
                method = "processNecUk";
                excel = 1;
                break;
            case "7010306":
                method = "processTransfast";
                excel = 1;
                break;
            case "7010300":
                method = "processMerchanTrade";
                excel = 1;
                break;
            case "7010239":
                method = "processAlAnsary";
                excel = 1;
                break;
            case "7010238":
                method = "processPrabhu";
                excel = 1;
                break;    
            case "7010291":
                method = "processAftab";
                excel = 1;
                break;
            case "7010286":
                method = "processPlacid";
                excel = 1;
                break;
            case "7010311":
                method = "proceesHelloPaisa";
                excel = 1;
                break;
            case "7010267":
                method = "processUremit";
                break;
            case "7010276":
                method = "processNblApi";
                break;
        }
        resp.put("excel", excel);
        resp.put("method", method);
        resp.put("exchangeCode", exchangeCode);
        return resp;
    }

    public Map<String, Object> processApi(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> dataResp = apiService.processApiData("5", csvRecords, countryList);
        return dataResp;
    }

    public Map<String, Object> processCbl(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = "458";
            //LocalDate paidDate = CommonService.convertStringToLocalDate(csvRecord.get(46).trim(),"dd-MMM-yy");
            //LocalDate enteredDate = CommonService.convertStringToLocalDate(csvRecord.get(1).trim(),"dd-MMM-yy");
            //System.out.println(csvRecord.get(46).trim());
            //System.out.println(csvRecord.get(1).trim());
            String rawDate = csvRecord.get(46);
            for (int i = 0; i < rawDate.length(); i++) {
    System.out.println(i + " : '" + rawDate.charAt(i) + "' -> " + (int)rawDate.charAt(i));
}
            if (rawDate != null && !rawDate.isBlank()) {
                String dateStr = rawDate.trim().replace("\u00A0", " ").replace("\u200B", "").replace("’","'").toUpperCase();
                //LocalDate paidDate = CommonService.convertStringToLocalDate(dateStr,"dd-MMM-yy");
                //System.out.println(paidDate);
                DateTimeFormatter formatter =DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH);
                try{
                    LocalDate date = LocalDate.parse(dateStr, formatter);
                    System.out.println(date);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            
            

            Map<String, Object> data = new HashMap<>();
            //String routingNo = CommonService.fixRoutingNo(csvRecord.get(8).trim());
            //Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(8).trim();
            String userId = csvRecord.get(42).trim();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(12).trim());
            data.put("remitterAddress", csvRecord.get(20).trim());
            data.put("remitterPassport", csvRecord.get(13).trim());
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(10).trim());
            data.put("beneficiaryMobile", csvRecord.get(17).trim());
            data.put("beneficiaryNid", "");
            //data.put("paidDate", paidDate.toString());
            //data.put("enteredDate", enteredDate.toString());
            data.put("userId", userId);
            //data.put("bankCode", routingDetails.get("bank_code"));
            //data.put("branchCode", routingDetails.get("abl_branch_code"));
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("purposeOfRemittance", csvRecord.get(25).trim());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            String[] fields = {"remitterMobile","sourceOfIncome"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNblApi(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = "";
            LocalDateTime paidDate = CommonService.convertStringToDate(csvRecord.get(11).trim(), "yyyy-MM-dd HH:mm:ss Z");
            //System.out.println(csvRecord.get(11).trim());
            //System.out.println(paidDate);
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(10).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(9).trim();
            String agentCode = csvRecord.get(6).trim();
            switch(agentCode){
                case "060":
                    nrtaCode = "7059";
                    exchangeCode = "7010276";
                    sourceCountry = "458";
                    break;
                case "030":
                    nrtaCode = "7076";
                    exchangeCode = "7010310";
                    sourceCountry = "300";
                    break;
                case "960":
                    nrtaCode = "7105";
                    exchangeCode = "7010297";
                    sourceCountry = "462";
                    break;
            }
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(5).trim());
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(2).trim());
            data.put("beneficiaryNid", csvRecord.get(4).trim());
            data.put("beneficiaryMobile", "");
            data.put("enteredDate", paidDate.toLocalDate().toString());
            data.put("paidDate", paidDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag",5);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processEzRemit(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(3), exchangeCode);
            LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(7).trim());
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(8).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(4).trim();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(1).trim());
            data.put("remitterAddress", csvRecord.get(2).trim());
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(5).trim());
            data.put("beneficiaryNid", csvRecord.get(6).trim());
            data.put("enteredDate", enteredDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            String[] fields = {"remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processInstantCash(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(3), exchangeCode);
            LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(8).trim());
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(7).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(4).trim();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(1).trim());
            data.put("remitterAddress", csvRecord.get(2).trim());
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(5).trim());
            data.put("beneficiaryAddress", csvRecord.get(6).trim());
            data.put("enteredDate", enteredDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processRia(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(4).substring(0,2), exchangeCode);
            LocalDate enteredDate = CommonService.convertStringToLocalDate(csvRecord.get(0).trim(),"MM/d/yyyy");
            LocalDate paidDate = CommonService.convertStringToLocalDate(csvRecord.get(1).trim(),"MM/d/yyyy");
            Map<String, Object> data = new HashMap<>();
            String branchCode = csvRecord.get(10).trim();
            //Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(11).trim();
            String amount = csvRecord.get(8).trim();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", "");
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", "");
            data.put("beneficiaryAddress", "");
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("branchCode", branchCode);
            //data.put("bankCode", routingDetails.get("bank_code"));
            //data.put("branchCode", routingDetails.get("abl_branch_code"));
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        System.out.println(dataList);
        return resp;
    }

    public Map<String, Object> processWesternUnion(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(31));
            String operatorId = CommonService.getCellValueAsString(row.getCell(135));
            String userId = (!operatorId.isEmpty()) ? operatorId: CommonService.getCellValueAsString(row.getCell(134));
            String amount = CommonService.getCellValueAsString(row.getCell(126));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(4)), exchangeCode);
            //Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(33)),"yyyyMMdd");
            //System.out.println(row.getCell(45));
            System.out.println(CommonService.getCellValueAsString(row.getCell(33)));
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("enteredDate", paidDate.toString());
            data.put("paidDate", paidDate.toString());
            //data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", userId);
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("sourceForeignCurrency", CommonService.getCellValueAsString(row.getCell(53)));
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","remitterName","remitterPassport","beneficiaryName"};
            for(String field: fields)   data.put(field, "");
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processMoneyGram(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        String legacyId= "";
        for (int rowIndex = 6; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String cellB = CommonService.getCellValueAsString(row.getCell(1)).trim();
            String cellC = CommonService.getCellValueAsString(row.getCell(2)).trim();
            if(cellB.trim().isEmpty() && cellC.trim().isEmpty()) continue;
            if(cellC.contains("Legacy ID :")) legacyId = CommonService.getCellValueAsString(row.getCell(6)).trim();
            if(cellB.contains("Account Number :") || cellB.isEmpty() || cellB.contains("Settlement Currency :")) continue;
            String amount = CommonService.getCellValueAsString(row.getCell(25)).trim();
            if(amount.equals("0.0")) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(8)).trim();
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(14)), exchangeCode);
            LocalDate paidDate = CommonService.convertStringToLocalDate(cellB, "MM/dd/yyyy");
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount.replace("-", ""));
            data.put("sourceCountry", sourceCountry);
            data.put("userId", legacyId);
            data.put("paidDate", paidDate.toString());
            data.put("enteredDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
        }
        System.out.println(dataList);
        System.out.println(dataList.size());
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNecItaly(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(8)).replace("NEC", "");
            String amount = CommonService.getCellValueAsString(row.getCell(12)).replace(",", "");
            //String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(3)), exchangeCode);
            String userId = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(5)).trim());
            //Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            LocalDate enteredDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(2)),"dd/MM/yyyy");
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(3)),"dd/MM/yyyy");
            Map<String, Object> beneficiaryDoc = CommonService.parseNecDoc(CommonService.getCellValueAsString(row.getCell(7)));
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", "");
            data.put("remitterAddress", "");
            data.put("sourceCountry", "");
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(11)));
            data.put("beneficiaryNid", beneficiaryDoc.get("nid").toString());
            data.put("remitterPassport", beneficiaryDoc.get("passport").toString());  
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            //data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", userId);
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNecUk(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(13));
            String amount = CommonService.getCellValueAsString(row.getCell(17));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(4)), exchangeCode);
            String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(9)).trim());
            //Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            //LocalDate enteredDate = CommonService.convertStringToLocalDate(row.getCell(2).toString(),"M/dd/yyyy");
            //LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(6)),"dd/MM/yyyy");
            System.out.println(CommonService.getCellValueAsString(row.getCell(2)));
            System.out.println(row.getCell(2));
            //System.out.println(enteredDate);
            Map<String, Object> beneficiaryDoc = CommonService.parseNecDoc(CommonService.getCellValueAsString(row.getCell(12)));
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(3)));
            data.put("remitterAddress", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(10)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(16)));
            data.put("beneficiaryAddress", CommonService.getCellValueAsString(row.getCell(11)));
            data.put("beneficiaryNid", beneficiaryDoc.get("nid").toString());
            data.put("remitterPassport", beneficiaryDoc.get("passport").toString());  
            //data.put("enteredDate", enteredDate.toString());
           // data.put("paidDate", paidDate.toString());
            //data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", branchCode);
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processTransfast(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        for (int rowIndex = 6; rowIndex < worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(1));
            String amount = CommonService.getCellValueAsString(row.getCell(19)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(21)), exchangeCode);
            String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(17)).trim()); //work later
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(4)), "MMMM dd, yyyy");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(3)),"MMMM dd, yyyy");
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(20)));
            data.put("beneficiaryAddress", "");
            data.put("enteredDate", enteredDate);
            data.put("paidDate", paidDate);
            //data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", branchCode);
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        System.out.println(dataList.size());
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processMerchanTrade(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(5));
            String amount = CommonService.getCellValueAsString(row.getCell(10)).replace(",", "");
            String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(13)).trim()); //work later
            LocalDate paidDate = CommonService.convertStringToLocalDate(row.getCell(2).toString(), "dd-MMM-yyyy");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(row.getCell(1).toString(),"dd-MMM-yyyy");
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", "458");
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("beneficiaryMobile", "");
            data.put("beneficiaryAddress", "");
            data.put("enteredDate", enteredDate);
            data.put("paidDate", paidDate);
            //data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", branchCode);
            //data.put("branchName", routingDetails.get("branch_name"));
            //data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        System.out.println(dataList.size());
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processAlAnsary(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 5; rowIndex < worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(1) == null || row.getCell(1).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(2));
            String amount = CommonService.getCellValueAsString(row.getCell(11)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(6)), exchangeCode);
            String branchCode = CommonService.getCellValueAsString(row.getCell(3)).trim(); //generate this id to branch later
            LocalDate paidDate = CommonService.convertStringToLocalDate(row.getCell(5).toString(),"dd-MMM-yyyy");
            LocalDate eneterdDate = CommonService.convertStringToLocalDate(row.getCell(5).toString(),"dd-MMM-yyyy");
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(8)));
            data.put("remitterMobile", "");
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(9)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(10)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", eneterdDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag",5);
            data.put("branchCode", branchCode);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processPrabhu(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(1)).replaceAll("[^\\x20-\\x7E]", "");
            String amount = CommonService.getCellValueAsString(row.getCell(20));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(11)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(33)).trim(); //generate this id to branch later
            String[] dateArr = row.getCell(32).toString().split(" ");
            LocalDate paidDate = CommonService.convertStringToLocalDate(dateArr[0],"dd-MMM-yyyy");
            String[] dateArr2 = row.getCell(2).toString().split(" ");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(dateArr2[0],"dd-MMM-yyyy");
            //LocalDateTime eneterdDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(10)));
            //System.out.println(row.getCell(2));
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(3)));
            data.put("remitterMobile", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterAddress", CommonService.getCellValueAsString(row.getCell(4)));
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(15)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(20)));
            data.put("beneficiaryAddress", CommonService.getCellValueAsString(row.getCell(16)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("purposeOfRemittance", CommonService.getCellValueAsString(row.getCell(13)));
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag",5);
            data.put("branchCode", userId);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> proceesAftab(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 2; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(0));
            String amount = CommonService.getCellValueAsString(row.getCell(4));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(3)), exchangeCode);
            String routingNo = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(8)).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            LocalDateTime enteredDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(7)),"yyyy-MM-dd HH:mm:ss Z");
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(1)));
            data.put("remitterAddress", CommonService.getCellValueAsString(row.getCell(2)));
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("beneficiaryNid", row.getCell(6));  //should work later
            data.put("enteredDate", enteredDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag",5);
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> proceesPlacid(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(3));
            String amount = CommonService.getCellValueAsString(row.getCell(9));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(1)), exchangeCode);
            String userId = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(13)).trim()); //generate this id to branch later
            LocalDateTime enteredDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(0)),"E MMM dd HH:mm:ss z yyyy");
            System.out.println(CommonService.getCellValueAsString(row.getCell(0)));
            System.out.println(enteredDate);
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", enteredDate.toLocalDate().toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            data.put("branchCode", userId);
            data.put("currency", "BDT");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> proceesHelloPaisa(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        //List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(4));
            String amount = CommonService.getCellValueAsString(row.getCell(16)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(7)), exchangeCode);
            String branchCode = CommonService.getCellValueAsString(row.getCell(2)).trim(); //generate this id to branch later
            branchCode = branchCode.toLowerCase().replace("officer br","").replace("officer  br","");
            LocalDateTime paidDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(12)));
            LocalDateTime eneterdDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(10)));
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterMobile", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(8)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(9)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", eneterdDate.toLocalDate());
            data.put("paidDate", paidDate.toLocalDate());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            data.put("branchCode", branchCode);
            data.put("currency", "BDT");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processUremit(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            //String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(3), exchangeCode);
            LocalDateTime paidDate = CommonService.convertStringToDate(csvRecord.get(9).trim(), "yyyy-MM-dd HH:mm:ss Z");
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(7).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(4).trim();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(1).trim());
            data.put("remitterAddress", "");
            data.put("sourceCountry", "");
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(2).trim());
            data.put("beneficiaryNid", csvRecord.get(5).trim());
            data.put("beneficiaryMobile", csvRecord.get(6).trim());
            data.put("enteredDate", paidDate.toLocalDate().toString());
            data.put("paidDate", paidDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag",5);
            data.put("currency", "BDT");
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        System.out.println(dataList);
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }
}
