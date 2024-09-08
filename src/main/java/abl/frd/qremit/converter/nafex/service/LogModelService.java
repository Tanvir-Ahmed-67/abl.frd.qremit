package abl.frd.qremit.converter.nafex.service;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import abl.frd.qremit.converter.nafex.model.LogModel;
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;

@Service
public class LogModelService {
    @Autowired
    LogModelRepository logModelRepository;

    public void findLogModelByErrorDataId(String errorDataId){
        LogModel logModel = logModelRepository.findByErrorDataId(errorDataId);
        List<HashMap<String, Object>> dataList = processInfoData(logModel.getInfo());

    }

    public List<HashMap<String, Object>> processInfoData(String infoJson){
        List<HashMap<String, Object>> dataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        // Parse the JSON string into a Map
        /* 
        Map<String, Object> infoMap = objectMapper.readValue(infoJson, HashMap.class);
        dataList.add((HashMap<String, Object>) infoMap.get("oldData"));
        dataList.add((HashMap<String, Object>) infoMap.get("updatedData"));
        */
        return dataList;
    }
}
