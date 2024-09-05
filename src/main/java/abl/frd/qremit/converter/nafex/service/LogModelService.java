package abl.frd.qremit.converter.nafex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abl.frd.qremit.converter.nafex.model.LogModel;
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;

@Service
public class LogModelService {
    @Autowired
    LogModelRepository logModelRepository;

    public void findLogModelByErrorDataId(String errorDataId){
        LogModel logModel = logModelRepository.findByErrorDataId(errorDataId);

    }

    public void processInfoData(){
        
    }
}
