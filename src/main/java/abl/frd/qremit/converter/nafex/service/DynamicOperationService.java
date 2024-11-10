package abl.frd.qremit.converter.nafex.service;
import abl.frd.qremit.converter.nafex.helper.RepositoryModelWrapper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.*;
@SuppressWarnings("unchecked")
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
    @Autowired
    private ApplicationContext context; // To fetch repositories dynamically
    private Map<String, RepositoryModelWrapper<?>> repositoryModelMap = new HashMap<>();
    private String packageName = "abl.frd.qremit.converter.nafex.model.";

    @Bean
    public Map<String, RepositoryModelWrapper<?>> repositoryModelMap(){
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAll();
        for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
            String className = packageName + exchangeHouseModel.getClassName();
            String repositoryName = exchangeHouseModel.getRepositoryName();
            String exchangeCode = exchangeHouseModel.getExchangeCode();
            if(!CommonService.checkEmptyString(className) && !CommonService.checkEmptyString(repositoryName)){
                try{
                    JpaRepository<?, ?> repository = (JpaRepository<?, ?>) context.getBean(repositoryName);
                    Class<?> modelClass = Class.forName(className);
                    repositoryModelMap.put(exchangeCode, new RepositoryModelWrapper<>((JpaRepository) repository, modelClass));
                }catch(ClassNotFoundException | BeansException e){
                    e.printStackTrace();
                }
            }else continue;
        }
        return repositoryModelMap;
    }

    public Map<String, Object> transferApiBeftnData(int fileInfoModelId) {
        Map<String, Object> resp = new HashMap<>();
        List<ApiBeftnModel> allRows = apiBeftnModelRepository.findAllByFileInfoModelId(fileInfoModelId);
        for (ApiBeftnModel row : allRows) {
            try {
                String exchangeCode = row.getExchangeCode();
                RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
                if (wrapper != null) {
                    JpaRepository repository = wrapper.getRepository();
                    Class<?> modelClass = wrapper.getModelClass();
                    Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
                    Object modelInstance = constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getUploadDateTime(), row.getFileInfoModel(), row.getUserModel());
                    repository.save(modelInstance);
                } else {
                    resp = CommonService.getResp(1, "No repository or model class found for exchangeCode: " + exchangeCode, null);
                    //throw new IllegalArgumentException("No repository or model class found for exchangeCode: " + exchangeCode);
                }
            } catch (Exception e) {
                // Handle exception
                return CommonService.getResp(1, e.getMessage(), null);
            }
        }
        resp = CommonService.getResp(0, "Information processed succesfully", null);
        resp.put("url", "/user-home-page");
        return resp;
    }
    public Map<String, Object> transferApiT24Data(int fileInfoModelId) {
        Map<String, Object> resp = new HashMap<>();
        List<ApiT24Model> allRows = apiT24ModelRepository.findAllByFileInfoModelId(fileInfoModelId);
        for (ApiT24Model row : allRows) {
            try {
                String exchangeCode = row.getExchangeCode();
                RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
                if (wrapper != null) {
                    JpaRepository repository = wrapper.getRepository();
                    Class<?> modelClass = wrapper.getModelClass();
                    Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
                    Object modelInstance = constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getUploadDateTime(), row.getFileInfoModel(), row.getUserModel());
                    repository.save(modelInstance);
                } else {
                    resp = CommonService.getResp(1, "No repository or model class found for exchangeCode: " + exchangeCode, null);
                    //throw new IllegalArgumentException("No repository or model class found for exchangeCode: " + exchangeCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CommonService.getResp(1, e.getMessage(), null);
            }
        }
        resp = CommonService.getResp(0, "Information processed succesfully", null);
        resp.put("url", "/user-home-page");
        return resp;
    }

    public Map<String, Object> transferErrorData(Map<String, Object> updatedData){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try{
            String exchangeCode = updatedData.get("exchangeCode").toString();
            RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
            if (wrapper != null) {
                JpaRepository repository = wrapper.getRepository();
                Class<?> modelClass = wrapper.getModelClass();
                Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
                
                Double amount = updatedData.get("amount") != null ? CommonService.convertStringToDouble(updatedData.get("amount").toString()) : null;
                int fileInfoId = updatedData.get("fileInfoId") != null ? CommonService.convertStringToInt(updatedData.get("fileInfoId").toString()) : 0;
                int userId = updatedData.get("userId") != null ? CommonService.convertStringToInt(updatedData.get("userId").toString()) : 0;
                if(fileInfoId == 0 || userId == 0)    return CommonService.getResp(1, "Invalid File Id or User Id", null);
                //LocalDateTime uploadDateTime = CommonService.convertStringToDate(updatedData.get("uploadDateTime").toString());
                String beneficiaryAccount= String.valueOf(updatedData.get("beneficiaryAccount"));
                String branchCode = String.valueOf(updatedData.get("branchCode"));
                String bankName = String.valueOf(updatedData.get("bankName"));
                String typeFlag = updatedData.get("typeFlag").toString();

                int checkT24 = (("1").equals(typeFlag))  ? 1:0; 
                int checkAccPayee = (("2").equals(typeFlag))  ? 1:0; 
                int checkBeftn = (("3").equals(typeFlag))  ? 1:0;
                int checkCoc = (("4").equals(typeFlag))  ? 1:0;
                FileInfoModel fileInfoModel = fileInfoModelRepository.findById(fileInfoId);
                Integer accPayeeCount = CommonService.convertStringToInt(fileInfoModel.getAccountPayeeCount()) + checkAccPayee;
                Integer beftnCount = CommonService.convertStringToInt(fileInfoModel.getBeftnCount()) + checkBeftn;
                Integer cocCount = CommonService.convertStringToInt(fileInfoModel.getCocCount()) + checkCoc;
                Integer t24Count = CommonService.convertStringToInt(fileInfoModel.getOnlineCount()) + checkT24;
                Integer totalCount = accPayeeCount + beftnCount + cocCount + t24Count;
                fileInfoModel.setAccountPayeeCount(String.valueOf(accPayeeCount));
                fileInfoModel.setBeftnCount(String.valueOf(beftnCount));
                fileInfoModel.setCocCount(String.valueOf(cocCount));
                fileInfoModel.setOnlineCount(String.valueOf(t24Count));
                fileInfoModel.setTotalCount(String.valueOf(totalCount));
                int erroCount = fileInfoModel.getErrorCount() - 1;
                fileInfoModel.setErrorCount(erroCount);
                User user = userModelRepository.findByUserId(userId);

                Object modelInstance = constructor.newInstance(exchangeCode, updatedData.get("transactionNo"), updatedData.get("currency"), amount, 
                    updatedData.get("enteredDate"), updatedData.get("remitterName"), updatedData.get("remitterMobile"), updatedData.get("beneficiaryName"), 
                    beneficiaryAccount, updatedData.get("beneficiaryMobile"), bankName, updatedData.get("bankCode"), 
                    updatedData.get("branchName"), branchCode, updatedData.get("draweeBranchName"), updatedData.get("draweeBranchCode"), 
                    updatedData.get("purposeOfRemittance"), updatedData.get("sourceOfIncome"), updatedData.get("processFlag"), typeFlag, 
                    updatedData.get("processedBy"), updatedData.get("processedDate"), currentDateTime, fileInfoModel, user);

                List<Object> modelInstanceList = new ArrayList<>();
                modelInstanceList.add(modelInstance);
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(modelInstanceList, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = (FileInfoModel)  convertedDataModels.get("fileInfoModel");           
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