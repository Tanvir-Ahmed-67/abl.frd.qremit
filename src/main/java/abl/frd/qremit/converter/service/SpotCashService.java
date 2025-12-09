package abl.frd.qremit.converter.service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.sl.usermodel.Sheet;
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
            Method dynamicMethod = this.getClass().getDeclaredMethod(methodName, CSVRecord.class);
            dynamicMethod.setAccessible(true);
            List<Map<String, Object>> dataList = new ArrayList<>();
            int i = 0;
            for(CSVRecord csvRecord: csvRecords){
                Map<String, Object> data = (Map<String, Object>) dynamicMethod.invoke(this, csvRecord, exchangeCode, nrtaCode, countryList);
                dataList.add(data);
            }
            System.out.println(dataList);
        } catch (Exception e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
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
                method = "proceessAftab";
                excel = 1;
                break;
            case "7010286":
                method = "proceessPlacid";
                excel = 1;
                break;
            case "7010311":
                method = "proceessHelloPaisa";
                excel = 1;
                break;
            case "7010267":
                method = "proceessUremit";
                excel = 1;
                break;
        }
        resp.put("excel", excel);
        resp.put("method", method);
        resp.put("exchangeCode", exchangeCode);
        return resp;
    }

    public Map<String, Object> processApi(CSVRecord csvRecord, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        System.out.println(csvRecord);
        return resp;
    }

    public Map<String, Object> processCbl(CSVRecord csvRecord, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        System.out.println(csvRecord);
        return resp;
    }

    public Map<String, Object> processEzRemit(CSVRecord csvRecord, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        System.out.println(csvRecord);
        return resp;
    }

    public Map<String, Object> processInstantCash(CSVRecord csvRecord, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        System.out.println(csvRecord);
        return resp;
    }

    public Map<String, Object> processRia(CSVRecord csvRecord, String exchangeCode, String nrtaCode, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        System.out.println(csvRecord);
        return resp;
    }
}
