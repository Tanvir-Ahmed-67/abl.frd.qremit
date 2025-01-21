package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.model.LogModel;
import abl.frd.qremit.converter.repository.LogModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
@SuppressWarnings("unchecked")
@Service
public class LogModelService {
    @Autowired
    LogModelRepository logModelRepository;

    public List<Map<String, Object>> findLogModelByDataId(String dataId){
        List<Map<String, Object>> logInfo = new ArrayList<>();
        LogModel logModel = logModelRepository.findByDataId(dataId);
        logInfo = processLogData(logModel);
        return logInfo;
    }

    public List<Map<String, Object>> processLogData(LogModel logModel){
        String infoJson = logModel.getInfo();
        List<Map<String, Object>> dataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Map<String, Object> infoMap = objectMapper.readValue(infoJson, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> oldData = new HashMap<>();
            Map<String, Object> updatedData = new HashMap<>();
            Map<String, Object> errorDataModel = new HashMap<>();
            Map<String, Object> errorDataModelMap = new HashMap<>();
            Map<String, Object> oldDataMap = new HashMap<>();
            Map<String, Object> updatedDataMap = new HashMap<>();
            Map<String, Object> exchangeCodeMap = new HashMap<>();
            Map<String, Object> userIdMap = new HashMap<>();
            Map<String, Object> dataIdMap = new HashMap<>();
            
            if(infoMap.containsKey("oldData"))  oldData = (Map<String, Object>) infoMap.get("oldData");
            if(infoMap.containsKey("updatedData"))  updatedData = (Map<String, Object>) infoMap.get("updatedData");
            if(infoMap.containsKey("errorDataModel"))  errorDataModel = (Map<String, Object>) infoMap.get("errorDataModel");
            oldDataMap.put("oldData",oldData);
            updatedDataMap.put("updatedData", updatedData);
            exchangeCodeMap.put("exchangeCode", logModel.getExchangeCode());
            userIdMap.put("userId", logModel.getUserId());
            dataIdMap.put("dataId", logModel.getDataId());
            errorDataModelMap.put("errorDataModel", errorDataModel);
            
            dataList.add(oldDataMap);
            dataList.add(updatedDataMap);
            dataList.add(exchangeCodeMap);
            dataList.add(dataIdMap);
            dataList.add(userIdMap);
            dataList.add(errorDataModelMap);

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

    public Map<String, Object> addLogModel(int userId, int fileInfoModelId, String exchangeCode, String errorDataModelId, String action, Map<String, Object> info, HttpServletRequest request){
        Map<String, Object> resp = CommonService.getResp(1, "Error Updating Information", null);
        String ipAddress = request.getRemoteAddr();
        String infoStr = CommonService.serializeInfoToJson(info);
        if(infoStr == null) return CommonService.getResp(1, "Failed to parse JSON data", null);
        try{
            LogModel logModel = new LogModel(String.valueOf(userId), errorDataModelId, fileInfoModelId, exchangeCode, action, infoStr, ipAddress);
            logModelRepository.save(logModel);
            resp = CommonService.getResp(0, "Data inserted to LogModel successful", null);
        }catch(Exception e){
            return CommonService.getResp(1, "Error saving LogModel: " + e.getMessage(), null);
        }
        return resp;
    }
}
