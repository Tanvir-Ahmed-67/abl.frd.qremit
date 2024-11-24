package abl.frd.qremit.converter.nafex.service;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import abl.frd.qremit.converter.nafex.repository.CustomQueryRepository;
@SuppressWarnings("unchecked")
@Service
public class CustomQueryService {
    @Autowired
    CustomQueryRepository customQueryRepository;
    
    public Map<String, Object> getFileDetails(String tableName, String fileInfoId) {
        return customQueryRepository.getFileDetails(tableName, fileInfoId);
    }

    public Map<String, Object> getRoutingDetails(String routingNo){
        return customQueryRepository.getRoutingDetails(routingNo);
    }

    public Map<String, Object> getRoutingDetailsByRoutingNo(String routingNo){
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> routingDetails = getRoutingDetails(routingNo);
        if((Integer) routingDetails.get("err") == 0){
            for(Map<String,Object> rdata: (List<Map<String, Object>>) routingDetails.get("data")){
                return rdata;
            }
        }
        return resp;
    }
}
