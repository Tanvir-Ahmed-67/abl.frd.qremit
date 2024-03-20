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
}
