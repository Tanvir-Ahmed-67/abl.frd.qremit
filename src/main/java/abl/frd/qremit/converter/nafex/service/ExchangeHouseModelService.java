package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        exchangeHouseModelRepository.setExchangeHouseActiveStatusTrueById(id);
        return true;
    }
    public void editExchangeHouse(ExchangeHouseModel exchangeHouseModel) throws Exception {
        exchangeHouseModelRepository.save(exchangeHouseModel);
        exchangeHouseModelRepository.setExchangeHouseActiveStatusFalseById(exchangeHouseModel.getId());
    }
}
