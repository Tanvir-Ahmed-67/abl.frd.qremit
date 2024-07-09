package abl.frd.qremit.converter.nafex.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.ReportRepository;

@Service
public class ReportService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    ReportRepository reportRepository;

    public ExchangeHouseModel findByExchangeCode(String exchangeCode){
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findByExchangeCode(exchangeCode);
        return exchangeHouseModel;
    }

    public Map<String,Object> getFileDetails(String tableName, String fileInfoId) {
        return reportRepository.getFileDetails(tableName, fileInfoId);
    }

}
