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
    @Autowired
    private ApiT24ModelRepository apiT24ModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    
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

    public void transferApiBeftnData() {
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
    public void transferApiT24Data() {
        List<ApiT24Model> allRows = apiT24ModelRepository.findAll();
        for (ApiT24Model row : allRows) {
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
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> transferErrorData(Map<String, Object> updatedData){
        Map<String, Object> resp = new HashMap<>();
        try{
            String exchangeCode = updatedData.get("exchangeCode").toString();
            RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
            if (wrapper != null) {
                JpaRepository repository = wrapper.getRepository();
                Class<?> modelClass = wrapper.getModelClass();
                Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, FileInfoModel.class, User.class);
                
                Double amount = updatedData.get("amount") != null ? Double.parseDouble(updatedData.get("amount").toString()) : null;
                Long fileInfoId = updatedData.get("fileInfoId") != null ? Long.parseLong(updatedData.get("fileInfoId").toString()) : null;
                Integer userId = updatedData.get("userId") != null ? Integer.parseInt(updatedData.get("userId").toString()) : null;
                if(fileInfoId == null || userId == null)    return CommonService.getResp(1, "Invalid File Id or User Id", null);

                String checkAccPayee = updatedData.get("checkAccPayee").toString();
                String checkBeftn = updatedData.get("checkBeftn").toString();
                String checkCoc = updatedData.get("checkCoc").toString();
                String checkT24 = updatedData.get("checkT24").toString();
                FileInfoModel fileInfoModel = fileInfoModelRepository.findById(fileInfoId);
                Integer accPayeeCount = Integer.parseInt(fileInfoModel.getAccountPayeeCount()) + Integer.parseInt(checkAccPayee);
                Integer beftnCount = Integer.parseInt(fileInfoModel.getBeftnCount()) + Integer.parseInt(checkBeftn);
                Integer cocCount = Integer.parseInt(fileInfoModel.getCocCount()) + Integer.parseInt(checkCoc);
                Integer t24Count = Integer.parseInt(fileInfoModel.getOnlineCount()) + Integer.parseInt(checkT24);
                Integer totalCount = accPayeeCount + beftnCount + cocCount + t24Count;
                fileInfoModel.setAccountPayeeCount(String.valueOf(accPayeeCount));
                fileInfoModel.setBeftnCount(String.valueOf(beftnCount));
                fileInfoModel.setCocCount(String.valueOf(cocCount));
                fileInfoModel.setOnlineCount(String.valueOf(t24Count));
                fileInfoModel.setTotalCount(String.valueOf(totalCount));
                User user = userModelRepository.findByUserId(userId);

                Object modelInstance = constructor.newInstance(exchangeCode, updatedData.get("transactionNo"), updatedData.get("currency"), amount, 
                    updatedData.get("enteredDate"), updatedData.get("remitterName"), updatedData.get("remitterMobile"), updatedData.get("beneficiaryName"), 
                    updatedData.get("beneficiaryAccount"), updatedData.get("beneficiaryMobile"), updatedData.get("bankName"), updatedData.get("bankCode"), 
                    updatedData.get("branchName"), updatedData.get("branchCode"), updatedData.get("draweeBranchName"), updatedData.get("draweeBranchCode"), 
                    updatedData.get("purposeOfRemittance"), updatedData.get("sourceOfIncome"), updatedData.get("processFlag"), updatedData.get("typeFlag"), 
                    updatedData.get("processedBy"), updatedData.get("processedDate"), "", checkT24, checkCoc, checkAccPayee, checkBeftn, fileInfoModel, user);
                
                List<OnlineModel> onlineModelList = CommonService.generatOnlineModelListFromErrorData(modelInstance,"getCheckT24","0");
                List<CocModel> cocModelList = CommonService.generatCocModelListFromErrorData(modelInstance, "getCheckCoc");
                List<AccountPayeeModel> accountPayeeModelList = CommonService.generatAccountPayeeModelListFromErrorData(modelInstance, "getCheckAccPayee");
                List<BeftnModel> beftnModelList = CommonService.generateBeftnModelListFromErrorData(modelInstance, "getCheckBeftn");
                fileInfoModel.setOnlineModelList(onlineModelList);
                fileInfoModel.setCocModelList(cocModelList);
                fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
                fileInfoModel.setBeftnModelList(beftnModelList);
                for (CocModel cocModel : cocModelList) {
                    cocModel.setFileInfoModel(fileInfoModel);
                    cocModel.setUserModel(user);
                }
                for (AccountPayeeModel accountPayeeModel : accountPayeeModelList) {
                    accountPayeeModel.setFileInfoModel(fileInfoModel);
                    accountPayeeModel.setUserModel(user);
                }
                for (BeftnModel beftnModel : beftnModelList) {
                    beftnModel.setFileInfoModel(fileInfoModel);
                    beftnModel.setUserModel(user);
                }
                for (OnlineModel onlineModel : onlineModelList) {
                    onlineModel.setFileInfoModel(fileInfoModel);
                    onlineModel.setUserModel(user);
                }
                                
                repository.save(modelInstance);
                resp = CommonService.getResp(0, "Information saved succesfully", null);
            } else {
                String msg = "No repository or model class found for cxchangeCode: " + exchangeCode;
                resp = CommonService.getResp(1, msg, null);
                throw new IllegalArgumentException(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = CommonService.getResp(1, "Error Updating Information", null);
        }
        return resp;
    }
}