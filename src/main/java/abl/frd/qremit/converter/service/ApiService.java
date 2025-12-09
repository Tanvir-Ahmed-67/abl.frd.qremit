package abl.frd.qremit.converter.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;

import java.time.LocalDateTime;
import java.util.*;;

@Service
public class ApiService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    CustomQueryService customQueryService;
    public Map<String, Object> processApiData(String type, Iterable<CSVRecord> csvRecords, List<Map<String, Object>> countryList){
        Map<String, Object> resp = new HashMap<>();
        int i= 0;
        List<String[]> uniqueKeys = new ArrayList<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        Map<String, String> nrtaCodeVsExchangeCodeMap = CommonService.getNrtaCodeVsExchangeCodeMap(exchangeHouseModelList);
        for (CSVRecord csvRecord : csvRecords) {
            String trMode = csvRecord.get(12).toString();
            if(CommonService.convertStringToInt(csvRecord.get(9).toString()) != 2) continue; //check data only status = 2
            String branchCode = CommonService.fixRoutingNo(csvRecord.get(8).trim());
            String beneficiaryAccount = csvRecord.get(7).trim();
            if("6".equals(type)){
                if(!CommonService.checkNpsb(trMode))    continue;
            }else if("5".equals(type)){
                if(!CommonService.checkSpotCash(trMode))    continue;
                if(!CommonService.checkAgraniRoutingNo(branchCode)) continue;
                if(beneficiaryAccount.toLowerCase().startsWith("coc"))   continue;
            }else continue;

            String nrtaCode = csvRecord.get(0).trim();
            String exchangeCode = nrtaCodeVsExchangeCodeMap.get(nrtaCode);
            String sourceCountry = customQueryService.parseCountryCode(countryList, csvRecord.get(14).trim(), exchangeCode);
            Map<String, Object> data = getData(csvRecord, exchangeCode, nrtaCode, type, sourceCountry, branchCode, beneficiaryAccount);
            dataList.add(data);
            String transactionNo = data.get("transactionNo").toString();
            String amount = data.get("amount").toString();
            uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            i++;
        }
        System.out.println(dataList.size());
        resp.put("dataList", dataList);
        resp.put("uniqueKeys", uniqueKeys);
        return resp;
    }

    public Map<String, Object> getData(CSVRecord csvRecord, String exchangeCode, String nrtaCode, String type, String sourceCountry, String branchCode, String beneficiaryAccount){
        String bankName = ""; 
        String bankCode = ""; 
        String branchName = "";
        LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(3).trim());
        LocalDateTime paidDate = CommonService.convertStringToDate(csvRecord.get(11).trim());
        String incentive = csvRecord.get(13).toString();
        Map<String, Object> data = new HashMap<>();
        data.put("typeFlag", type);
        data.put("exchangeCode", exchangeCode);
        data.put("nrtaCode", nrtaCode);
        data.put("transactionNo", csvRecord.get(1).trim());
        data.put("amount", csvRecord.get(4).trim());
        data.put("enteredDate", enteredDate.toLocalDate().toString());
        data.put("paidDate", paidDate);
        data.put("remitterName", csvRecord.get(5).trim());
        data.put("beneficiaryName", csvRecord.get(6).trim());
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(10).trim());
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("currency", "BDT");
        data.put("incentive", incentive);
        data.put("govtIncentive", incentive);
        data.put("sourceCountry", sourceCountry);
        String[] fields = {"remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance"};
        for(String field: fields)   data.put(field, "");
        return data;
    }
}
