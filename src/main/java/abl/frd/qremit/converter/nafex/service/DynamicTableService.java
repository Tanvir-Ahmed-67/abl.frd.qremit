package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicTableService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    private Map<String, String> repositoryMap = new HashMap<>();
    private final Map<String, JpaRepository> repositoryInstanceMap = new HashMap<>();


    // Generating map of repositories identified by nrta code
    public Map<String, JpaRepository> generateAllRepositoryMap(){
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        String baseTableName;
        String nrtaCode;
        for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
            if(exchangeHouseModel.getExchangeCode().equals("710000")){
                continue;
            }
            baseTableName = exchangeHouseModel.getBaseTableName();
            baseTableName = baseTableName.substring(0, 1).toUpperCase() + baseTableName.substring(1);
            nrtaCode = exchangeHouseModel.getNrtaCode();
            //baseTableName = baseTableName.substring(0,1).toUpperCase() + baseTableName.substring(1).toLowerCase();
            String repositoryName = baseTableName+"ModelRepository";
            System.out.println(repositoryName);
            this.repositoryMap.put(nrtaCode, repositoryName);
        }
        this.repositoryMap.forEach((code, repositoryClassName) -> {
            try {
                Class<?> repositoryClass = Class.forName(repositoryClassName);
                JpaRepository repository = (JpaRepository) repositoryClass.getDeclaredConstructor().newInstance();
                this.repositoryInstanceMap.put(code, repository);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize repository: " + repositoryClassName, e);
            }
        });
        return this.repositoryInstanceMap;
    }

    public FileInfoModel saveApiBeftn(MultipartFile file, int userId) {
        generateAllRepositoryMap();
        //JpaRepository repository = repositoryInstanceMap.get("71000");
        System.out.println(repositoryInstanceMap);
        return null;
    }
}
