package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExchangeHouseModelService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;

    public List<ExchangeHouseModel> loadAllExchangeHouse(){
        List<ExchangeHouseModel> exchangeHouseModels = exchangeHouseModelRepository.findAll();
        return exchangeHouseModels;
    }
    public List<ExchangeHouseModel> loadAllActiveExchangeHouse(){
        List<ExchangeHouseModel> exchangeHouseModels = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        return exchangeHouseModels;
    }
    public ExchangeHouseModel getExchangeHouseByExchangeId(int exchangeId){
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findByExchangeId(exchangeId);
        return exchangeHouseModel;
    }
    public void insertNewExchangeHouse(ExchangeHouseModel exchangeHouseModel) throws Exception{
        exchangeHouseModelRepository.save(exchangeHouseModel);
    }
    public List<ExchangeHouseModel> loadAllInactiveExchangeHouse(){
        List<ExchangeHouseModel> inactiveExchangeHouseModels = exchangeHouseModelRepository.findAllInactiveExchangeHouse();
        return inactiveExchangeHouseModels;
    }

    public boolean updateInactiveExchangeHouse(int id){
        //exchangeHouseModelRepository.setExchangeHouseActiveStatusTrueById(id);
        exchangeHouseModelRepository.setExchangeHouseActiveStatusById(id, 1);
        return true;
    }
    public void editExchangeHouse(ExchangeHouseModel exchangeHouseModel) throws Exception {
        exchangeHouseModelRepository.editExchangeHouse(exchangeHouseModel.getExchangeName(), exchangeHouseModel.getExchangeShortName(), exchangeHouseModel.getNrtaCode(), exchangeHouseModel.getId());
    }

    public ExchangeHouseModel findByExchangeCode(String exchangeCode){
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findByExchangeCode(exchangeCode);
        return exchangeHouseModel;
    }

    public List<ExchangeHouseModel> loadAllIsSettlementExchangeHouse(int isSettlement){
        return exchangeHouseModelRepository.findAllExchangeHouseByIsSettlement(isSettlement);
    }

    public List<ExchangeHouseModel> findAllByExchangeCodeIn(Set<String> exchangeCodes){
        return exchangeHouseModelRepository.findAllByExchangeCodeIn(exchangeCodes);
    }

    public Integer calculateSumOfHasSettlementDaily(List<ExchangeHouseModel> exchangeHouseModelList){
        int hasSettlementDailyCount = exchangeHouseModelList.stream().mapToInt(ExchangeHouseModel::getHasSettlementDaily).sum();
        return hasSettlementDailyCount;
    }

    public Map<String, Object> getExchangeHouse(String exchangeCode, String nrtaCode){
        Map<String, Object> resp = new HashMap<>();
        ExchangeHouseModel exchangeHouseModel;
        String msg = "Exchange Code and Nrta Code Mismatched";
        if(exchangeCode.isEmpty() && nrtaCode.isEmpty())    return CommonService.getResp(1, "Please select Exchange Code or Nrta Code", null);
        if(!exchangeCode.isEmpty()){
            exchangeHouseModel = exchangeHouseModelRepository.findByExchangeCode(exchangeCode);
            if(exchangeHouseModel != null){
                if(!nrtaCode.isEmpty()){
                    if(!nrtaCode.equals(exchangeHouseModel.getNrtaCode()))  return CommonService.getResp(1, msg, null);
                }
                resp =  CommonService.getResp(0, "", null);
                resp.put("data", exchangeHouseModel);
            }
        }
        if(!nrtaCode.isEmpty()){
            exchangeHouseModel = exchangeHouseModelRepository.findExchangeHouseModelByNrtaCode(nrtaCode);
            if(exchangeHouseModel != null){
                if(!exchangeCode.isEmpty()){
                    if(!exchangeCode.equals(exchangeHouseModel.getExchangeCode()))  return CommonService.getResp(1, msg, null);
                }
                resp =  CommonService.getResp(0, "", null);
                resp.put("data", exchangeHouseModel);
            }
        }
        return resp;
    }
    public Map<String, String> getExchangeNamesByCodes(List<String> exchangeCodes) {
        if (exchangeCodes == null || exchangeCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        // Fetch all exchange house models matching the provided codes
        List<ExchangeHouseModel> exchangeHouseModels = exchangeHouseModelRepository.findByExchangeCodeIn(exchangeCodes);
        // Convert the list into a map of exchangeCode to exchangeName
        return exchangeHouseModels.stream()
                .collect(Collectors.toMap(ExchangeHouseModel::getExchangeCode, ExchangeHouseModel::getExchangeName));
    }
}
