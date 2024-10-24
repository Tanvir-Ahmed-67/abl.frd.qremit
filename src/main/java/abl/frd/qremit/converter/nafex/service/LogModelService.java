package abl.frd.qremit.converter.nafex.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import abl.frd.qremit.converter.nafex.model.LogModel;
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;
import java.util.*;
@SuppressWarnings("unchecked")
@Service
public class LogModelService {
    @Autowired
    LogModelRepository logModelRepository;

    public List<Map<String, Object>> findLogModelByErrorDataId(String errorDataId){
        List<Map<String, Object>> logInfo = new ArrayList<>();
        LogModel logModel = logModelRepository.findByErrorDataId(errorDataId);
        logInfo = processLogData(logModel);
        //System.out.println(logInfo);
        return logInfo;
    }

    public List<Map<String, Object>> processLogData(LogModel logModel){
        String infoJson = logModel.getInfo();
        List<Map<String, Object>> dataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Map<String, Object> infoMap = objectMapper.readValue(infoJson, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> oldData = (Map<String, Object>) infoMap.get("oldData");
            Map<String, Object> updatedData = (Map<String, Object>) infoMap.get("updatedData");
            Map<String, Object> oldDataMap = new HashMap<>();
            Map<String, Object> updatedDataMap = new HashMap<>();
            Map<String, Object> exchangeCodeMap = new HashMap<>();
            Map<String, Object> userIdMap = new HashMap<>();
            Map<String, Object> errorDataIdMap = new HashMap<>();

            oldDataMap.put("oldData",oldData);
            updatedDataMap.put("updatedData", updatedData);
            exchangeCodeMap.put("exchangeCode", logModel.getExchangeCode());
            userIdMap.put("userId", logModel.getUserId());
            errorDataIdMap.put("errorDataId", logModel.getErrorDataId());
            
            dataList.add(oldDataMap);
            dataList.add(updatedDataMap);
            dataList.add(exchangeCodeMap);
            dataList.add(errorDataIdMap);
            dataList.add(userIdMap);

        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return dataList;
    }

    public Map<String, Object> fetchLogDataByKey(List<Map<String, Object>> logInfo, String key){
        Map<String, Object> resp = new HashMap<>();
        for (Map<String, Object> dataMap : logInfo) {
            if (dataMap.containsKey(key))   return (Map<String, Object>) dataMap.get(key);        
        }
        return resp;
    }
}
