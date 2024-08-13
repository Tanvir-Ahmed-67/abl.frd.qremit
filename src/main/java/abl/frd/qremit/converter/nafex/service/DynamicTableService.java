package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.ApiBeftnModel;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
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
    @Autowired
    private ApiBeftnModelRepository apiBeftnModelRepository;
    @Autowired
    private ApiT24ModelRepository apiT24ModelRepository;

    LocalDateTime currentDateTime = LocalDateTime.now();
    private final Map<String, JpaRepository> repositoryInstanceMap = new HashMap<>();
    private final ApplicationContext applicationContext;

    public void transferData() {
        DoSomething();
        List<ApiBeftnModel> allRows = apiBeftnModelRepository.findAll();
        for (ApiBeftnModel row : allRows) {
            String ExchangeCode = row.getExchangeCode();
            JpaRepository<?, Long> repository = repositoryInstanceMap.get(ExchangeCode);
            if (repository != null) {
                try {
                    Class<?> entityClass = repository.getClass().getDeclaredMethod("getDomainClass").invoke(repository).getClass();
                    Object entityInstance = entityClass.getDeclaredConstructor().newInstance();
                    entityClass.getMethod("setData", String.class).invoke(entityInstance, row);
                    // Cast and save the entity instance
                    saveEntity(repository, entityInstance);
                } catch (Exception e) {
                    // Handle exception
                    e.printStackTrace();
                }
            } else {
                // Handle case where repository is not found for a code
                System.out.println("No repository found for code: " + ExchangeCode);
            }
        }
    }

    private <T> void saveEntity(JpaRepository<T, Long> repository, Object entityInstance) {
        T entity = (T) entityInstance; // Cast the entity to the correct type
        repository.save(entity); // Save the entity
    }

    @Autowired
    public DynamicTableService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<String, JpaRepository> DoSomething() {
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        String modelClassName;
        String exchangeCode;
        for (ExchangeHouseModel exchangeHouseModel : exchangeHouseModelList) {
            try {
                if (exchangeHouseModel.getExchangeCode().equals("710000") || exchangeHouseModel.getExchangeCode().equals("720000")) {
                    continue;
                }
                modelClassName = exchangeHouseModel.getModelClassName();
                exchangeCode = exchangeHouseModel.getExchangeCode();
                String repositoryClassName = modelClassName;
                Class<?> repositoryClass = Class.forName("abl.frd.qremit.converter.nafex.repository." + repositoryClassName);
                JpaRepository repository = (JpaRepository) applicationContext.getBean(repositoryClass);
                if (repository == null) {
                    throw new IllegalArgumentException("No repository found for bean name: " + repositoryClass);
                }
                this.repositoryInstanceMap.put(exchangeCode, repository);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize repository: " + e.getMessage());
            }
        }
        return repositoryInstanceMap;
    }
}