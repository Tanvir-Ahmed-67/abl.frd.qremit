package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.RepositoryModelWrapper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicOperationService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    private ApiBeftnModelRepository apiBeftnModelRepository;

    LocalDateTime currentDateTime = LocalDateTime.now();
    private Map<String, RepositoryModelWrapper<?>> repositoryModelMap = new HashMap<>();


    @Bean
    public Map<String, RepositoryModelWrapper<?>> repositoryModelMap(
            AgexSingaporeModelRepository agexSingaporeModelRepository,
            BecModelRepository becModelRepository,
            EzRemitModelRepository ezRemitModelRepository,
            MuzainiModelRepository muzainiModelRepository,
            NafexModelRepository nafexModelRepository,
            RiaModelRepository riaModelRepository) {
        repositoryModelMap.put("7010226", new RepositoryModelWrapper<>(agexSingaporeModelRepository, AgexSingaporeModel.class));
        repositoryModelMap.put("7010209", new RepositoryModelWrapper<>(becModelRepository, BecModel.class));
        repositoryModelMap.put("7010299", new RepositoryModelWrapper<>(ezRemitModelRepository, EzRemitModel.class));
        repositoryModelMap.put("7010231", new RepositoryModelWrapper<>(muzainiModelRepository, MuzainiModel.class));
        repositoryModelMap.put("7010234", new RepositoryModelWrapper<>(nafexModelRepository, NafexEhMstModel.class));
        repositoryModelMap.put("7010290", new RepositoryModelWrapper<>(riaModelRepository, RiaModel.class));
        return repositoryModelMap;
    }

    public void transferData() {
        List<ApiBeftnModel> allRows = apiBeftnModelRepository.findAll();
        for (ApiBeftnModel row : allRows) {
            try {
                String exchangeCode = row.getExchangeCode();
                RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
                if (wrapper != null) {
                    JpaRepository repository = wrapper.getRepository();
                    Class<?> modelClass = wrapper.getModelClass();
                    Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, FileInfoModel.class, User.class);
                    Object modelInstance = constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getExtraC(), row.getCheckT24(), row.getCheckCoc(), row.getCheckAccPayee(), row.getCheckBeftn(), row.getFileInfoModel(), row.getUserModel());
                    repository.save(modelInstance);
                } else {
                    throw new IllegalArgumentException("No repository or model class found for cxchangeCode: " + exchangeCode);
                }
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
    }
}