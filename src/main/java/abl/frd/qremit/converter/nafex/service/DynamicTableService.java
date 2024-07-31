package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicTableService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    private final Map<String, JpaRepository> repositoryInstanceMap = new HashMap<>();
    private final ApplicationContext applicationContext;

    @Autowired
    public  DynamicTableService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    public Map<String, JpaRepository> DoSomething(){
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        String modelClassName;
        String nrtaCode;
        for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
            try{
                if(exchangeHouseModel.getExchangeCode().equals("710000")){
                    continue;
                }
                modelClassName = exchangeHouseModel.getModelClassName();
                nrtaCode = exchangeHouseModel.getNrtaCode();
                String repositoryClassName = modelClassName+"Repository";
                Class<?> repositoryClass = Class.forName("abl.frd.qremit.converter.nafex.repository."+repositoryClassName);
                JpaRepository repository = (JpaRepository) applicationContext.getBean(repositoryClass);
                if (repository == null) {
                    throw new IllegalArgumentException("No repository found for bean name: " + repositoryClass);
                }
                this.repositoryInstanceMap.put(nrtaCode, repository);
            }catch (Exception e) {
                throw new RuntimeException("Failed to initialize repository: " + e.getMessage());
            }
        }
        return repositoryInstanceMap;
    }

    public FileInfoModel saveApiBeftn(MultipartFile file, int userId) {
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            DoSomething();
            //JpaRepository repository = repositoryInstanceMap.get("71000");
            System.out.println(repositoryInstanceMap);
            return null;
        } catch (Exception e) {
        throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
}
