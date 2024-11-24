package abl.frd.qremit.converter.service;
import java.time.LocalDateTime;
import java.util.*;

import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.FileInfoModelDTO;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class FileInfoModelService {
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    public List<FileInfoModel> getUploadedFileDetails(int userId, String date){
        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(date);
        if(userId != 0){
            return fileInfoModelRepository.getUploadedFileDetails(userId, dateTime.get("startDateTime"), dateTime.get("endDateTime"));
        }else{
            return fileInfoModelRepository.getUploadedFileDetails(dateTime.get("startDateTime"), dateTime.get("endDateTime"));
        }
    }

    public Map<String, Object> deleteFileInfoModelById(int id){
        Map<String, Object> resp = CommonService.getResp(1,"Data not deleted", null);
        try{    
            if(fileInfoModelRepository.existsById(id)){
                fileInfoModelRepository.deleteById(id);
                if(!fileInfoModelRepository.existsById(id)) resp = CommonService.getResp(0, "Data deleted succesful", null);
            }else   resp = CommonService.getResp(1, "Data not exists", null);
        }catch(Exception e){
            e.printStackTrace();
            resp = CommonService.getResp(1, "Error Occured ", null);
        }
        return resp;
    }

    public List<FileInfoModel> getFileDetailsBetweenUploadedDate(String startDate, String endDate){
        startDate += " 00:00:00";
        endDate += " 23:59:59";
        LocalDateTime startDateTime = CommonService.convertStringToDate(startDate);
        LocalDateTime enDateTime = CommonService.convertStringToDate(endDate);
        return fileInfoModelRepository.getFileDetailsBetweenUploadedDate(startDateTime, enDateTime);
    }

    public FileInfoModelDTO getSettlementDataByExchangeCode(String date, String exchangeCode, int isSettlement){
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        LocalDateTime startDateTime = CommonService.convertStringToDate(startDate);
        LocalDateTime endDateTime = CommonService.convertStringToDate(endDate);
        return fileInfoModelRepository.getSettlementDataByExchangeCode(startDateTime, endDateTime, exchangeCode, isSettlement);
    }

    public List<Map<String, Object>> getSettlementList(List<ExchangeHouseModel> exchangeHouseModelList, String date){
        List<Map<String, Object>> settlementList = new ArrayList<>();        
        for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
            String exchangeCode = exchangeHouseModel.getExchangeCode();
            FileInfoModelDTO fileInfoModelDTO = getSettlementDataByExchangeCode(date, exchangeCode, 1);
            Map<String, Object> resp = new HashMap<>();
            resp.put("exchangeCode", exchangeCode);
            resp.put("exchangeName", exchangeHouseModel.getExchangeName());
            resp.put("hasSettlementDaily", exchangeHouseModel.getHasSettlementDaily());
            int count = 0;
            if(fileInfoModelDTO != null){
                count = fileInfoModelDTO.getCount();
                resp.put("fileInfoModel", fileInfoModelDTO.getFileInfoModel());
            }
            resp.put("count", count);
            settlementList.add(resp);
        }
        return settlementList;
    }

    public FileInfoModel findAllById(int id){
        return fileInfoModelRepository.findById(id);
    }

}
