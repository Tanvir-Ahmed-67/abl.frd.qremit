package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class NafexModelService {
    @Autowired
    NafexModelRepository nafexModelRepository;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    public String save(MultipartFile file) {
        String numberOfRows=null;
        try
        {
            List<NafexEhMstModel> nafexModels = NafexModelServiceHelper.csvToNafexModels(file.getInputStream());
            for(NafexEhMstModel nafexModel : nafexModels){
                nafexModel.setExchangeCode("7010234");
            }

            //Map<String, List<Object>> differentTypesOfModels = NafexModelServiceHelper.segregateDifferentTypesOfModel(nafexModels);

            List<OnlineModel> onlineModelList = NafexModelServiceHelper.generateOnlineModelList(nafexModels);
            List<CocModel> cocModelList = NafexModelServiceHelper.generateCocModelList(nafexModels);
            List<AccountPayeeModel> accountPayeeModelList = NafexModelServiceHelper.generateAccountPayeeModelList(nafexModels);
            List<BeftnModel> beftnModelList = NafexModelServiceHelper.generateBeftnModelList(nafexModels);

            System.out.println("........"+onlineModelList);

            nafexModelRepository.saveAll(nafexModels);
            onlineModelRepository.saveAll(onlineModelList);
            cocModelRepository.saveAll(cocModelList);
            accountPayeeModelRepository.saveAll(accountPayeeModelList);
            beftnModelRepository.saveAll(beftnModelList);

            numberOfRows = String.valueOf(nafexModelRepository.count());
            return numberOfRows;
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
}
