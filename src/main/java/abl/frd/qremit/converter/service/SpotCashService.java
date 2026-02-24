package abl.frd.qremit.converter.service;
import org.apache.commons.csv.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.SpotCashModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.SpotCashRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.io.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    SpotCashRepository spotCashRepository;
    private String exchangeCodeSc;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode, String exchangeCodeSc){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        String tbl = "spotcash";
        this.exchangeCodeSc = exchangeCodeSc;
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setUserModel(user);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            List<FileInfoModel> fileInfoModelList = new ArrayList<>();
            Map<String, Object> config = getExchangeConfig(exchangeCode);
            Map<String, Object> spotCashData = new HashMap<>();
            int excel = (int) config.get("excel");

            Map<String, Object> dataResp = new HashMap<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            String duplicateMessage = "";
            int duplicateCount = 0;
            
            if(excel == 1){
                dataResp = processExcelData(file.getInputStream(), user, fileInfoModel, currentDateTime, exchangeCode, nrtaCode, config, tbl);
            }else{
                dataResp = processCsvData(file.getInputStream(), user, fileInfoModel, currentDateTime, exchangeCode, nrtaCode, config, tbl);
            }

            Optional<SpotCashModel> duplicateData = Optional.empty();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataResp.get("dataList");
            List<String[]> uniqueKeys = (List<String[]>) dataResp.get("uniqueKeys");
            if(dataList.isEmpty())  return CommonService.getResp(0, "No data found for processing", null);
            int totalCount = dataList.size();
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = new HashMap<>();
            Map<String, Object> modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, SpotCashModel.class, resp, errorDataModelList, "",0,0);
            List<SpotCashModel> spotCashModelList = (List<SpotCashModel>) modelResp.get("modelList");
            duplicateMessage = modelResp.get("duplicateMessage").toString();
            duplicateCount = (int) modelResp.get("duplicateCount");
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, totalCount));
            }
            if(spotCashData.containsKey("errorMessage")){
                resp.put("errorMessage", spotCashData.get("errorMessage"));
            }
            if(spotCashModelList.isEmpty()) return resp;
            int spotCashCount = 0;
            double totalAmount = 0;
            for(SpotCashModel spotCashModel: spotCashModelList){
                if(!spotCashModel.getBranchCode().isEmpty()){
                    spotCashModel.setIsDownloaded(1);
                    spotCashModel.setIsProcessed(1);
                    spotCashModel.setEntryActive(1);
                    spotCashModel.setDownloadDateTime(currentDateTime);
                    spotCashModel.setDownloadUserId(userId);
                }
                double amount = spotCashModel.getAmount();
                totalAmount += amount;
                double govtIncentive = CommonService.calculateGovtIncentivePercentage(amount);
                double agraniIncentive = CommonService.calculateAgraniIncentivePercentage(amount);
                spotCashModel.setGovtIncentive(govtIncentive);
                spotCashModel.setAgraniIncentive(agraniIncentive);
                spotCashModel.setIncentive(govtIncentive + agraniIncentive);
                spotCashCount += 1;
                spotCashModel.setFileInfoModel(fileInfoModel);
                spotCashModel.setUserModel(user);
            }
            Map<String, Object> convertedData = new HashMap<>();
            convertedData.put("spotCashCount", spotCashCount);
            fileInfoModel = CommonService.setCountForFileInfoModel(fileInfoModel, convertedData);
            fileInfoModel.setTotalAmount(CommonService.convertNumberFormat(totalAmount, 2));
            fileInfoModel.setSpotCashModelList(spotCashModelList);
            fileInfoModelRepository.save(fileInfoModel);  
            fileInfoModelList.add(fileInfoModel);
            resp.put("err" , 0);              
            resp.put("data", fileInfoModelList);
        }catch(Exception e){
            e.printStackTrace();
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
        return resp;
    }

    public Map<String, Object> processCsvData(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String exchangeCode, 
        String nrtaCode, Map<String, Object> config, String tbl){
        List<Map<String, Object>> countryList = customQueryService.getCountryList();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            String methodName = (String) config.get("method");
            Method dynamicMethod = this.getClass().getDeclaredMethod(methodName, Iterable.class, String.class, String.class,List.class);
            dynamicMethod.setAccessible(true);
            Map<String, Object> dataResp = (Map<String, Object>) dynamicMethod.invoke(this, csvRecords, exchangeCode, nrtaCode, countryList);
            return dataResp;
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
        
    }

    public Map<String, Object> processExcelData(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String exchangeCode, 
        String nrtaCode, Map<String, Object> config, String tbl){
        List<Map<String, Object>> countryList = customQueryService.getCountryList();
        try{
            Workbook records = CommonService.getWorkbook(is);
            Sheet worksheet = records.getSheetAt(0);
            String methodName = (String) config.get("method");
            Method dynamicMethod = this.getClass().getDeclaredMethod(methodName, Sheet.class, String.class, String.class,List.class);
            dynamicMethod.setAccessible(true);
            Map<String, Object> dataResp = (Map<String, Object>) dynamicMethod.invoke(this, worksheet, exchangeCode, nrtaCode, countryList);
            return dataResp;
        }catch(Exception e){
            e.printStackTrace();
            String msg = "fail to store csv data: " + e.getMessage();
            return CommonService.getResp(1, msg, null);
        }
    }

    public Map<String, Object> getExchangeConfig(String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        int excel = 0;
        String method = "";
        switch(exchangeCode){
            case "7010226":
            case "7010228":
            case "777777":
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
                //excel = 1;
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
            case "888888":
                method = "processNblApi";
                break;
            case "7010309":
                method = "processXpressMoney";
                break;
        }
        resp.put("excel", excel);
        resp.put("method", method);
        resp.put("exchangeCode", exchangeCode);
        return resp;
    }

    public Map<String, Object> processApi(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        Map<String, Object> dataResp = apiService.processApiData("5", csvRecords, countryList, routingData);
        return dataResp;
    }

    public Map<String, Object> processCbl(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = "458";
            String paidDateStr = CommonService.parseCblDate(csvRecord.get(46).trim());
            LocalDate paidDate = CommonService.convertStringToLocalDate(paidDateStr,"dd-MMM-yy");
            String enterdDateStr = CommonService.parseCblDate(csvRecord.get(1).trim());
            LocalDate enteredDate = CommonService.convertStringToLocalDate(enterdDateStr,"dd-MMM-yy");
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(8).trim();
            String userId = csvRecord.get(42).trim();
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, "", routingData);
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(12).trim());
            data.put("remitterAddress", csvRecord.get(20).trim());
            data.put("remitterPassport", csvRecord.get(13).trim());
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(10).trim());
            data.put("beneficiaryMobile", csvRecord.get(17).trim());
            data.put("beneficiaryNid", "");
            data.put("paidDate", paidDate.toString());
            data.put("enteredDate", enteredDate.toString());
            data.put("paidUserId", userId);
            data.put("purposeOfRemittance", csvRecord.get(25).trim());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNblApi(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        Map<String, Object> nrtaCodeVsExchangeDetailsMap = CommonService.getNrtaCodeVsExchangeDetailsMap(exchangeHouseModelList);
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = "";
            LocalDateTime paidDate = CommonService.convertStringToDate(csvRecord.get(11).trim(), "yyyy-MM-dd HH:mm:ss Z");
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(10).trim());
            Map<String, Object> routingDetails = commonService.convertAblRoutingToBranchCode(routingNo, routingData);
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(9).trim();
            String agentCode = csvRecord.get(6).trim();
            switch(agentCode){
                case "060":
                case "60":
                    nrtaCode = "7059";
                    break;
                case "030":
                case "30":
                    nrtaCode = "7076";
                    break;
                case "960":
                    nrtaCode = "7105";
                    break;
                default:
                    nrtaCode = "";
                    break;
            }
            ExchangeHouseModel exchangeHouseModel = (ExchangeHouseModel) nrtaCodeVsExchangeDetailsMap.get(nrtaCode);
            exchangeCode = exchangeHouseModel.getExchangeCode();
            sourceCountry = exchangeHouseModel.getCountryCode();
            exchangeCodeSc = exchangeHouseModel.getExchangeCodeSc();
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(5).trim());
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryNid", csvRecord.get(4).trim());
            data.put("beneficiaryMobile", "");
            data.put("paidUserId", routingNo);
            data.put("enteredDate", paidDate.toLocalDate().toString());
            data.put("paidDate", paidDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount","beneficiaryName"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
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
            data.put("paidUserId", routingNo);
            data.put("beneficiaryNid", csvRecord.get(6).trim());
            data.put("enteredDate", enteredDate.toLocalDate().toString());
            data.put("paidDate", enteredDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code").toString());
            data.put("branchCode", routingDetails.get("abl_branch_code").toString());
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount"};
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
            LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(8).trim(), "yyyy-MM-dd HH:mm:ss Z");
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
            data.put("paidDate", enteredDate.toLocalDate().toString());
            data.put("bankCode", routingDetails.get("bank_code"));
            data.put("branchCode", routingDetails.get("abl_branch_code"));
            data.put("branchName", routingDetails.get("branch_name"));
            data.put("bankName", routingDetails.get("bank_name"));
            data.put("exchangeCode", exchangeCode);
            data.put("paidUserId", routingNo);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount"};
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
            if(csvRecord.get(0).isEmpty())  continue;
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(4).substring(0,2), exchangeCode);
            LocalDate enteredDate = CommonService.convertStringToLocalDate(csvRecord.get(6).trim(),"M/d/yyyy");
            LocalDate paidDate = CommonService.convertStringToLocalDate(csvRecord.get(5).trim(),"M/d/yyyy");
            Map<String, Object> data = new HashMap<>();
            String userId = csvRecord.get(10).trim();
            String branchCode = CommonService.fixAblBranchCode(userId);
            data = commonService.mapAblBranchDetails(data, branchCode, routingData);
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
            data.put("paidUserId", userId);
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }
    
    public Map<String, Object> processWesternUnion(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(31));
            String operatorId = CommonService.getCellValueAsString(row.getCell(135));
            String userId = (!operatorId.isEmpty()) ? operatorId: CommonService.getCellValueAsString(row.getCell(134));
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            String amount = CommonService.getCellValueAsString(row.getCell(126));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(4)), exchangeCode);
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(33)),"yyyyMMdd");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, "", routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("enteredDate", paidDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("paidUserId", userId);
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("sourceForeignCurrency", CommonService.getCellValueAsString(row.getCell(53)));
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","remitterName","remitterPassport","beneficiaryName","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
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
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
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
            amount = amount.replace("-", "");
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, legacyId);
            String transactionNo = CommonService.getCellValueAsString(row.getCell(8)).trim();
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(14)), exchangeCode);
            LocalDate paidDate = CommonService.convertStringToLocalDate(cellB, "MM/dd/yyyy");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, "", routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("sourceCountry", sourceCountry);
            data.put("paidUserId", legacyId);
            data.put("paidDate", paidDate.toString());
            data.put("enteredDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryNid","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount","beneficiaryName"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNecItaly(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(8)).replace("NEC", "");
            String amount = CommonService.getCellValueAsString(row.getCell(12)).replace(",", "");
            String userId = CommonService.getCellValueAsString(row.getCell(5));
            String branchCode = CommonService.removeAllSpecialCharacterFromString(userId);
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            branchCode = CommonService.fixAblBranchCode(branchCode.toLowerCase().replace("br", ""));
            LocalDate enteredDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(2)),"dd/MM/yyyy");
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(3)),"dd/MM/yyyy");
            Map<String, Object> beneficiaryDoc = CommonService.parseNecDoc(CommonService.getCellValueAsString(row.getCell(7)));
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, branchCode, routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", "");
            data.put("remitterAddress", "");
            data.put("sourceCountry", "380");
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(11)));
            data.put("beneficiaryNid", beneficiaryDoc.get("nid").toString());
            data.put("remitterPassport", beneficiaryDoc.get("passport").toString());  
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("paidUserId", userId);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processNecUk(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(13));
            String amount = CommonService.getCellValueAsString(row.getCell(17));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(4)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(8));
            String branchCode = CommonService.removeAllSpecialCharacterFromString(userId);
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            branchCode = CommonService.fixAblBranchCode(branchCode.toLowerCase().replace("br", ""));
            
            String paidDateStr = CommonService.getCellValueAsString(row.getCell(6)).trim();
            String paidDatePattern = CommonService.detectNecUKYearPattern(paidDateStr);
            LocalDate paidDate = CommonService.convertStringToLocalDate(paidDateStr, paidDatePattern);
            String enteredDateStr = CommonService.getCellValueAsString(row.getCell(2)).trim();
            String enteredDatePattern = CommonService.detectNecUKYearPattern(enteredDateStr);
            LocalDate enteredDate = CommonService.convertStringToLocalDate(enteredDateStr, enteredDatePattern);

            Map<String, Object> beneficiaryDoc = CommonService.parseNecDoc(CommonService.getCellValueAsString(row.getCell(12)));
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, branchCode, routingData);
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
            data.put("paidUserId", userId);  
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processTransfast(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 6; rowIndex < worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(1));
            String amount = CommonService.getCellValueAsString(row.getCell(19)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(21)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(17)).trim();
            String branchCode = CommonService.removeAllSpecialCharacterFromString(userId).toUpperCase().replace("BR", "");
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            LocalDate paidDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(4)), "MMMM dd, yyyy");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(3)),"MMMM dd, yyyy");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, branchCode, routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(20)));
            data.put("beneficiaryAddress", "");
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("paidUserId", userId); 
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processMerchanTrade(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(5));
            String amount = CommonService.getCellValueAsString(row.getCell(10)).replace(",", "");
            String userId = CommonService.getCellValueAsString(row.getCell(13)).trim();
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            LocalDate paidDate = CommonService.convertStringToLocalDate(row.getCell(2).toString(), "dd-MMM-yyyy");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(row.getCell(1).toString(),"dd-MMM-yyyy");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, "", routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", "458");
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(7)));
            data.put("beneficiaryMobile", "");
            data.put("beneficiaryAddress", "");
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("paidUserId", userId); 
            data.put("exchangeCode", exchangeCode);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processAlAnsary(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 5; rowIndex < worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(1) == null || row.getCell(1).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(2));
            String amount = CommonService.getCellValueAsString(row.getCell(11)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(6)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(3)).trim();
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId.substring(1));
            LocalDate paidDate = CommonService.convertStringToLocalDate(row.getCell(5).toString(),"dd-MMM-yyyy");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, "", routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(8)));
            data.put("remitterMobile", "");
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(9)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(10)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", paidDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("typeFlag","5");
            data.put("paidUserId", userId);
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processPrabhu(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(1)).replaceAll("[^\\x20-\\x7E]", "");
            String amount = CommonService.getCellValueAsString(row.getCell(20));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(11)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(33)).trim();
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, userId);
            String branchCode = CommonService.parseNumberFromString(userId);
            String[] dateArr = row.getCell(32).toString().split(" ");
            LocalDate paidDate = CommonService.convertStringToLocalDate(dateArr[0],"dd-MMM-yyyy");
            String[] dateArr2 = row.getCell(2).toString().split(" ");
            LocalDate enteredDate = CommonService.convertStringToLocalDate(dateArr2[0],"dd-MMM-yyyy");
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, branchCode, routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(3)));
            data.put("remitterMobile", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterAddress", CommonService.getCellValueAsString(row.getCell(4)));
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(15)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(19)));
            data.put("beneficiaryAddress", CommonService.getCellValueAsString(row.getCell(16)));
            data.put("beneficiaryNid", "");
            data.put("purposeOfRemittance", CommonService.getCellValueAsString(row.getCell(13)));
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", paidDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("currency", "BDT");
            data.put("sourceForeignCurrency", CommonService.getCellValueAsString(row.getCell(25)));
            data.put("typeFlag","5");
            data.put("paidUserId", userId);
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"sourceOfIncome","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processAftab(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(3), exchangeCode);
            LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(7).trim(), "yyyy-MM-dd HH:mm:ss Z");
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(9).trim());
            data = commonService.mapAblBranchDetails(data, routingNo, routingData);
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
            data.put("paidDate", enteredDate.toLocalDate().toString());
            data.put("exchangeCode", exchangeCode);
            data.put("paidUserId", routingNo);
            data.put("currency", "BDT");
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryAddress","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processPlacid(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        List<Map<String, Object>> branchUserDetails = customQueryService.getBranchUser("");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(3));
            String amount = CommonService.getCellValueAsString(row.getCell(9));
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(1)), exchangeCode);
            String paidUserId = CommonService.getCellValueAsString(row.getCell(13)).trim();
            Map<String, Object> branchDetails = customQueryService.getBranchUserDetailsByUserId(branchUserDetails, exchangeCodeSc, paidUserId);
            String userId = CommonService.fixAblBranchCode(paidUserId.replace("ABL", ""));
            LocalDate enteredDate = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(0)), "M/d/yy");
            
            Map<String, Object> data = new HashMap<>(); 
            data = commonService.mapAblUserIdToBranchdetails(data, branchDetails, userId, routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", enteredDate.toString());
            data.put("paidDate", enteredDate.toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("currency", "BDT");
            data.put("paidUserId", paidUserId);
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"beneficiaryAddress","remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance","beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> proceesHelloPaisa(Sheet worksheet, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        Row row;
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++){
            row = worksheet.getRow(rowIndex);
            if(row == null) continue;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
            String transactionNo = CommonService.getCellValueAsString(row.getCell(4));
            String amount = CommonService.getCellValueAsString(row.getCell(16)).replace(",", "");
            String sourceCountry = customQueryService.parseCountryCode(countryList, CommonService.getCellValueAsString(row.getCell(7)), exchangeCode);
            String userId = CommonService.getCellValueAsString(row.getCell(2)).trim(); //generate this id to branch later
            String branchCode = CommonService.fixAblBranchCode(userId.toLowerCase().replace("officer br","").replace("officer  br",""));
            LocalDateTime paidDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(12)));
            LocalDateTime eneterdDate = CommonService.convertStringToDate(CommonService.getCellValueAsString(row.getCell(10)));
            Map<String, Object> data = new HashMap<>();
            data = commonService.mapAblBranchDetails(data, branchCode, routingData);
            data.put("transactionNo", transactionNo);
            data.put("amount", amount);
            data.put("remitterName", CommonService.getCellValueAsString(row.getCell(5)));
            data.put("remitterMobile", CommonService.getCellValueAsString(row.getCell(6)));
            data.put("remitterAddress", "");
            data.put("sourceCountry", sourceCountry);
            data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(8)));
            data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(9)));
            data.put("beneficiaryNid", "");  //should work later
            data.put("enteredDate", eneterdDate.toLocalDate().toString());
            data.put("paidDate", paidDate.toLocalDate().toString());
            data.put("exchangeCode", exchangeCode);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("currency", "BDT");
            data.put("paidUserId", userId);
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            String[] fields = {"sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
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
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(4).trim();
            data = commonService.mapAblBranchDetails(data, routingNo, routingData);
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
            data.put("exchangeCode", exchangeCode);
            data.put("paidUserId", routingNo);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            data.put("currency", "BDT");
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> processXpressMoney(Iterable<CSVRecord> csvRecords, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> routingData = customQueryService.getRoutingDetailsByBankCode("010");
        for (CSVRecord csvRecord : csvRecords) {
            Map<String, Object> data = new HashMap<>();
            String routingNo = CommonService.fixRoutingNo(csvRecord.get(7).trim());
            String transactionNo = csvRecord.get(0).trim();
            String amount = csvRecord.get(4).trim();
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(3), exchangeCode);
            data = commonService.mapAblBranchDetails(data, routingNo, routingData);
            String enteredDate = csvRecord.get(8).trim().substring(0, 10);
            //LocalDate enteredDate = CommonService.convertStringToLocalDate(dateStr, "yyyy-MM-dd");
            data.put("transactionNo", transactionNo);
            data.put("remitterName", csvRecord.get(1).trim());
            data.put("remitterAddress", csvRecord.get(2).trim());
            data.put("sourceCountry", sourceCountry);
            data.put("amount", amount);
            data.put("beneficiaryName", csvRecord.get(5).trim());
            data.put("beneficiaryAddress", csvRecord.get(6).trim());
            data.put("enteredDate", enteredDate);
            data.put("paidDate", enteredDate);
            data.put("exchangeCode", exchangeCode);
            data.put("paidUserId", routingNo);
            data.put("nrtaCode", nrtaCode);
            data.put("typeFlag","5");
            data.put("exchangeCodeSc", this.exchangeCodeSc);
            data.put("currency", "BDT");
            String[] fields = {"remitterMobile","sourceOfIncome","purposeOfRemittance", "beneficiaryAccount"};
            for(String field: fields)   data.put(field, "");
            dataList.add(data);
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
        }
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> getExchangeWiseSummary(String date, int generateBtn){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime startDateTime = CommonService.convertStringToDate(date + " 00:00:00");
        LocalDateTime endDateTime = CommonService.convertStringToDate(date+ " 23:59:59");
        List<Object[]> spotCashModelList = spotCashRepository.getExchangeWiseDailyDataByDate(startDateTime, endDateTime);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> btnList = new HashMap<>();
        if(spotCashModelList.size() > 0){
            int i = 1;
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.getSpotCashExchangeList();
            int totalCount = 0;
            double totalAmount = 0;
            for(Object[] row: spotCashModelList){
                String exchangeCodeSc = (String) row[0];
                Double amount = (Double) row[1];
                long cnt = (Long) row[2];
                totalAmount += amount;
                totalCount += (int) cnt;
                String exchangeName = "";
                for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
                    if(exchangeCodeSc.equals(exchangeHouseModel.getExchangeCodeSc())){
                        exchangeName = exchangeHouseModel.getExchangeName();
                        break;
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("sl", i++);
                data.put("totalAmount", CommonService.convertNumberFormat(amount, 2));
                data.put("totalCount", cnt);
                data.put("exchangeCodeSc", exchangeCodeSc);
                data.put("exchangeName", exchangeName);
                dataList.add(data);
            }
            Map<String, Object> totalData = new HashMap<>();
            totalData.put("sl", "");
            totalData.put("totalAmount", CommonService.convertNumberFormat(totalAmount, 2));
            totalData.put("totalCount", totalCount);
            totalData.put("exchangeCodeSc", "");
            totalData.put("exchangeName", "Total Uploaded");
            dataList.add(totalData);
        }
        resp.put("data", dataList);
        resp.put("generateBtn", generateBtn);
        return resp;
    }

    public List<SpotCashModel> getProcessedDataByUploadDate(int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return spotCashRepository.getProcessedDataByUploadDate(isProcessed, isVoucherGenerated, 1, starDateTime, enDateTime);
    }

    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        spotCashRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }

    public List<SpotCashModel> findSpotCashModelByFileInfoModelIdAndIsVoucherGenerated(int fileInfoModelId){
        return spotCashRepository.findSpotCashModelByFileInfoModelIdAndIsVoucherGenerated(fileInfoModelId, 1);
    }
}
