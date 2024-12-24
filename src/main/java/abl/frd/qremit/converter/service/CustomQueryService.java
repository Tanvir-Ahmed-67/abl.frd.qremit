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

    public Map<String, Object> getRoutingDetailsByRoutingNo(String routingNo, String bankCode){
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> routingDetails = getRoutingDetails(routingNo, bankCode);
        if((Integer) routingDetails.get("err") == 0){
            for(Map<String,Object> rdata: (List<Map<String, Object>>) routingDetails.get("data")){
                return rdata;
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
}
