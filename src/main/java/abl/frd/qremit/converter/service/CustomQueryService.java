package abl.frd.qremit.converter.service;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import abl.frd.qremit.converter.repository.CustomQueryRepository;
@SuppressWarnings("unchecked")
@Service
public class CustomQueryService {
    @Autowired
    CustomQueryRepository customQueryRepository;
    
    public Map<String, Object> getFileDetails(String tableName, String fileInfoId) {
        return customQueryRepository.getFileDetails(tableName, fileInfoId);
    }
    
    public Map<String, Object> getFileTotalExchangeWise(String date, int userId){
        String starDateTime = date + " 00:00:00";
        String endDateTime = date + " 23:59:59";
        return customQueryRepository.getFileTotalExchangeWise(starDateTime, endDateTime, userId);
    }
    

    public Map<String, Object> getRoutingDetails(String routingNo, String bankCode){
        return customQueryRepository.getRoutingDetails(routingNo, bankCode);
    }

    public Map<String, Object> getRoutingDetailsByAblBranchCode(String branchCode){
        return customQueryRepository.getRoutingDetailsByAblBranchCode(branchCode);
    }

    public Map<String, Object> getBankListFromRouting(String bankCode){
        return customQueryRepository.getBankListFromRouting(bankCode);
    }

    public Map<String, Object> getRoutingDetailsByRoutingNo(String routingNo){
        Map<String, Object> resp = new HashMap<>();
        if(routingNo.isEmpty()) return CommonService.getResp(1, "Routing No is Empty", null);
        Map<String, Object> routingDetails = getRoutingDetails(routingNo, "");
        if((Integer) routingDetails.get("err") == 0){
            for(Map<String,Object> rdata: (List<Map<String, Object>>) routingDetails.get("data")){
                return rdata;
            }
        }
        return resp;
    }

    public List<Map<String,Object>> getRoutingDetailsByBankCode(String bankCode){
        Map<String, Object> routingDetails = new HashMap<>();
        List<Map<String, Object>> routingData = new ArrayList<>();
        routingDetails = getRoutingDetails("", bankCode);
        if((Integer) routingDetails.get("err") == 0){
            routingData = (List<Map<String, Object>>) routingDetails.get("data");
        }
        return routingData;
    }
    public Map<String, Object> generateRoutingDetailsByRoutingNo(List<Map<String,Object>> routingData, String routingNo){
        String key = "routing_no";
        return generateRoutingDetailsByRoutingNo(routingData, routingNo, key);
    }
    public Map<String, Object> generateRoutingDetailsByRoutingNo(List<Map<String,Object>> routingData, String routingNo, String key){
        Map<String, Object> resp = new HashMap<>();
        if(!routingData.isEmpty()){
            for(Map<String, Object> rdata: routingData){
                if(rdata.get(key).equals(routingNo)){
                    return rdata;
                }
            }
        }
        return resp;
    }

    public Map<String, Object> getBranchDetailsFromSwiftCode(String swiftCode){
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> swiftDetails = customQueryRepository.getBranchDetailsFromSwiftCode(swiftCode);
        if((Integer) swiftDetails.get("err") == 0){
            for(Map<String,Object> rdata: (List<Map<String, Object>>) swiftDetails.get("data")){
                return rdata;
            }
        }
        return resp;
    }

    public Map<String, Object> calculateTotalAmountForConvertedModel(int type, int fileInfoModelId){
        String tableName = "";
        switch(type){
            case 1:
                tableName = "online";
                break;
            case 2:
                tableName = "account_payee";
                break;
            case 3:
                tableName = "beftn";
                break;
            case 4:
                tableName = "coc";
                break;
        }
        tableName = "converted_data_" + tableName;
        if(type == 5)   tableName = "base_data_table_coc_paid";
        return customQueryRepository.calculateTotalAmountForConvertedModel(tableName, fileInfoModelId);
    }

    public Map<String, Object> getUniqueList(List<String[]> data, String tbl){
        return customQueryRepository.getUniqueListByTransactionNoAndAmountAndExchangeCodeIn(data, tbl);
    }

    public Map<String, Object> getArchiveUniqueList(List<String[]> data, String year){
        return customQueryRepository.getArchiveUniqueList(data, year);
    }

    public Map<String, Object> processArchiveUniqueList(List<String[]> data){
        Map<String, Object> archive_24 = getArchiveUniqueList(data, "2024");
        return archive_24;
    }

    public List<Map<String, Object>> getCountryList(){
        List<Map<String, Object>> countryList = new ArrayList<>();
        Map<String, Object> resp =  customQueryRepository.getCountry("", "", "", "");
        if((Integer) resp.get("err") == 0){
            countryList = (List<Map<String, Object>>) resp.get("data");
        }
        return countryList;
    }

    public Map<String, Object> getCountryFromList(List<Map<String, Object>> countryList, String key, String value){
        Map<String, Object> resp = new HashMap<>();
        if(countryList.isEmpty())   return resp;
        for(Map<String, Object> country: countryList){
            if(country.get(key).equals(value)){
                return country;
            }
        }
        return resp;
    }

    public String parseCountryCode(List<Map<String, Object>> countryList, String value, String exchangeCode){
        value = value.toUpperCase();
        if(value.isEmpty()) return "";
        if(exchangeCode.equals("7010232") && ("BANGLADESH").equals(value))  return "414";
        if(exchangeCode.equals("7010207") && ("KUWA").equals(value))    return "414";
        if(exchangeCode.equals("7010250") && ("BANGLADESH").equals(value))  return "512";
        if(exchangeCode.equals("7010237") && ("BD").equals(value))  return "512";
        if(exchangeCode.equals("7010296") && ("BD").equals(value))  return "702";
        if(exchangeCode.equals("7010209") && ("BD").equals(value))  return "414";
        if(exchangeCode.equals("7010228"))  return "458";
        if(exchangeCode.equals("7010226"))  return "702";
        if(("BD").equals(value) || ("BANGLADESH").equals(value))    return "";
        if(("UAE").equals(value) || ("UNITED ARAB EMIRATES").equals(value) || ("DF").equals(value))   return "784";
        if(("UK").equals(value) || ("UNITED KINGDOM").equals(value))   return "826";
        if(("USA").equals(value) || ("UNITED STATES OF AMERICA").equals(value) || ("UNITED STATES").equals(value))   return "840";
        String key = "";
        if(value.length() == 2) key = "two_digit";
        else if(value.length() == 3) key = "three_digit";
        else key = "country_name";
        Map<String, Object> country = getCountryFromList(countryList, key, value);
        String countryCode = "";
        if(country.size() > 0) countryCode =  country.get("country_code").toString();
        return countryCode;
    }

    public List<Map<String, Object>> getBranchUser(String branchCode){
        Map<String, Object> branchUserDetails = customQueryRepository.getBranchUser(branchCode);
        List<Map<String, Object>> branchUserList = new  ArrayList<>();
        if((Integer) branchUserDetails.get("err") == 0){
            branchUserList = (List<Map<String, Object>>) branchUserDetails.get("data");
        }
        return branchUserList;
    }

    public Map<String, Object> getBranchUserDetailsByUserId(List<Map<String,Object>> branchList, String exCodeSc, String userId){
        Map<String, Object> resp = new HashMap<>();
        if(branchList.isEmpty() || userId == null)  return resp;
        String key = exCodeSc + "id";
        for(Map<String, Object> bList: branchList){
            if(userId.equals(bList.get(key)))  return bList;
            if(exCodeSc.equals("m01")){
                if(userId.equals(bList.get(key + "1"))) return bList;
                if(userId.equals(bList.get(key + "2"))) return bList;
            }
        }
        return resp;
    }

}
