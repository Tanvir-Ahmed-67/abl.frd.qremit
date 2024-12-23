package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.helper.RepositoryModelWrapper;
import abl.frd.qremit.converter.model.*;
import abl.frd.qremit.converter.repository.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private String packageName = "abl.frd.qremit.converter.model.";
    @Autowired
    CustomQueryRepository customQueryRepository;
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

    public Map<String, RepositoryModelWrapper<?>> repositoryModelMapByExchangeCode(String exchangeCode){
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelRepository.findByExchangeCode(exchangeCode);
        String className = packageName + exchangeHouseModel.getClassName();
        String repositoryName = exchangeHouseModel.getRepositoryName();
        if(!CommonService.checkEmptyString(className) && !CommonService.checkEmptyString(repositoryName)){
            try{
                JpaRepository<?, ?> repository = (JpaRepository<?, ?>) context.getBean(repositoryName);
                Class<?> modelClass = Class.forName(className);
                repositoryModelMap.put(exchangeCode, new RepositoryModelWrapper<>((JpaRepository) repository, modelClass));
            }catch(ClassNotFoundException | BeansException e){
                e.printStackTrace();
            }
        }
        return repositoryModelMap;
    }

    public String getRepositoryErrorMsg(String exchangeCode){
        return "No repository or model class found for exchangeCode: " + exchangeCode;
    }

    public void findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(String exchangeCode){
        RepositoryModelWrapper<?> wrapper = (RepositoryModelWrapper<?>) repositoryModelMapByExchangeCode(exchangeCode);
        System.out.println(wrapper);
    }

    public Map<String, Object> transferApiBeftnData(int fileInfoModelId) {
        Map<String, Object> resp = new HashMap<>();
        int batchSize = 100; // Process in batches
        int page = 0;
        boolean hasMoreData = true;
        JpaRepository repository = null;
        try {
            while (hasMoreData) {
                Pageable pageable = PageRequest.of(page, batchSize);
                Page<ApiBeftnModel> pageResult = apiBeftnModelRepository.findByFileInfoModelId(fileInfoModelId, pageable);
                List<ApiBeftnModel> allRows = pageResult.getContent();
                hasMoreData = pageResult.hasNext();
                List<Object> entitiesToSave = new ArrayList<>();
                for (ApiBeftnModel row : allRows) {
                    String exchangeCode = row.getExchangeCode();
                    RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
                    if (wrapper != null) {
                        repository = wrapper.getRepository();
                        Class<?> modelClass = wrapper.getModelClass();
                        // Assuming a factory method or builder is available to replace reflection
                        Object modelInstance = createModelInstanceForApiBeftn(modelClass, row);
                        entitiesToSave.add(modelInstance);
                    } else {
                        return CommonService.getResp(1, getRepositoryErrorMsg(exchangeCode), null);
                    }
                }
                // Save batch
                if (!entitiesToSave.isEmpty()) {
                    repository.saveAll(entitiesToSave);
                }
                page++;
            }
            resp = CommonService.getResp(0, "Information processed successfully", null);
            resp.put("url", "/user-home-page");
        } catch (Exception e) {
            return CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }

    private Object createModelInstanceForApiBeftn(Class<?> modelClass, ApiBeftnModel row) {
        try {
            // Locate the appropriate constructor
            Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
            // Create and return the instance using the data from `row`
            return constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getUploadDateTime(), row.getFileInfoModel(), row.getUserModel());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create model instance for " + modelClass.getName(), e);
        }
    }

    public Object createModelInstanceForSwift(Class<?> modelClass, SwiftModel row) {
        try {
            // Locate the appropriate constructor
            Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
            // Create and return the instance using the data from `row`
            return constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getUploadDateTime(), row.getFileInfoModel(), row.getUserModel());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create model instance for " + modelClass.getName(), e);
        }
    }
    public Map<String, Object> transferApiBeftnData_1(int fileInfoModelId) {
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
                    resp = CommonService.getResp(1, getRepositoryErrorMsg(exchangeCode), null);
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
        int batchSize = 100; // Process in batches
        int page = 0;
        boolean hasMoreData = true;
        JpaRepository repository = null;
        try {
            while (hasMoreData) {
                Pageable pageable = PageRequest.of(page, batchSize);
                Page<ApiT24Model> pageResult = apiT24ModelRepository.findAllByFileInfoModelId(fileInfoModelId, pageable);
                List<ApiT24Model> allRows = pageResult.getContent();
                hasMoreData = pageResult.hasNext();
                List<Object> entitiesToSave = new ArrayList<>();
                for (ApiT24Model row : allRows) {
                    String exchangeCode = row.getExchangeCode();
                    RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
                    if (wrapper != null) {
                        repository = wrapper.getRepository();
                        Class<?> modelClass = wrapper.getModelClass();
                        // Assuming a factory method or builder is available to replace reflection
                        Object modelInstance = createModelInstanceForAPIt24(modelClass, row);
                        entitiesToSave.add(modelInstance);
                    } else {
                        return CommonService.getResp(1, getRepositoryErrorMsg(exchangeCode), null);
                    }
                }
                // Save batch
                if (!entitiesToSave.isEmpty()) {
                    repository.saveAll(entitiesToSave);
                }
                page++;
            }
            resp = CommonService.getResp(0, "Information processed successfully", null);
            resp.put("url", "/user-home-page");
        } catch (Exception e) {
            return CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }
    private Object createModelInstanceForAPIt24(Class<?> modelClass, ApiT24Model row) {
        try {
            // Locate the appropriate constructor
            Constructor<?> constructor = modelClass.getConstructor(String.class, String.class, String.class, Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, LocalDateTime.class, FileInfoModel.class, User.class);
            // Create and return the instance using the data from `row`
            return constructor.newInstance(row.getExchangeCode(), row.getTransactionNo(), row.getCurrency(), row.getAmount(), row.getEnteredDate(), row.getRemitterName(), row.getRemitterMobile(), row.getBeneficiaryName(), row.getBeneficiaryAccount(), row.getBeneficiaryMobile(), row.getBankName(), row.getBankCode(), row.getBranchName(), row.getBranchCode(), row.getDraweeBranchName(), row.getDraweeBranchCode(), row.getPurposeOfRemittance(), row.getSourceOfIncome(), row.getProcessFlag(), row.getTypeFlag(), row.getProcessedBy(), row.getProcessedDate(), row.getUploadDateTime(), row.getFileInfoModel(), row.getUserModel());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create model instance for " + modelClass.getName(), e);
        }
    }
    public Map<String, Object> transferApiT24Data_1(int fileInfoModelId) {
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
                    resp = CommonService.getResp(1, getRepositoryErrorMsg(exchangeCode), null);
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
                Double amount = updatedData.get("amount") != null ? CommonService.convertStringToDouble(updatedData.get("amount").toString()) : 0.0;
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
                String msg = getRepositoryErrorMsg(exchangeCode);
                resp = CommonService.getResp(1, msg, null);
                throw new IllegalArgumentException(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = CommonService.getResp(1, "Error Updating Information", null);
        }
        return resp;
    }

    public Map<String, Object> deleteFilInfoModelById(String exchangeCode, int fileInfoModelId){
        Map<String, Object> resp = new HashMap<>();
        String[] exchangeList = new String[]{"111111","222222","444444"};
        try{
            RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exchangeCode);
            if(wrapper != null){
                JpaRepository repository = wrapper.getRepository();
                Class<?> modelClass = wrapper.getModelClass();
                String entityName = modelClass.getSimpleName();
                List<?> records = new ArrayList<>();
                if(repository instanceof ApiBeftnModelRepository){
                    ApiBeftnModelRepository apiBeftnModelRepository = (ApiBeftnModelRepository) repository;
                    records = apiBeftnModelRepository.findAllByFileInfoModelIdOrderByExchangeCodeAsc(fileInfoModelId);
                }else if(repository instanceof ApiT24ModelRepository){
                    ApiT24ModelRepository apiT24ModelRepository = (ApiT24ModelRepository) repository;
                    records = apiT24ModelRepository.findAllByFileInfoModelIdOrderByExchangeCodeAsc(fileInfoModelId);
                }else if(repository instanceof SwiftModelRepository){
                    SwiftModelRepository swiftModelRepository = (SwiftModelRepository) repository;
                    records = swiftModelRepository.findAllByFileInfoModelIdOrderByExchangeCodeAsc(fileInfoModelId);
                }
                resp = customQueryRepository.deleteByFileInfoModelId(entityName, fileInfoModelId);
                if((Integer) resp.get("err") == 0){
                    if((Integer) resp.get("affectedRows") == 0)  return CommonService.getResp(1, "No data found for delete", null);
                    if(Arrays.asList(exchangeList).contains(exchangeCode)){
                        return deleteIndividualBeftnOrApiOrSwiftData(exchangeCode, fileInfoModelId, records);
                    }
                    
                }else return resp;
            }else resp = CommonService.getResp(1, getRepositoryErrorMsg(exchangeCode), null);
        }catch(Exception e){
            e.printStackTrace();
            resp = CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }
    
    public Map<String, Object> deleteIndividualBeftnOrApiOrSwiftData(String exchangeCode, int fileInfoModelId, List<?> records){
        Map<String, Object> resp = new HashMap<>();
        String[] exchangeList = new String[]{"111111","222222","444444"};
        if(!Arrays.asList(exchangeList).contains(exchangeCode)){
            return CommonService.getResp(1, "Invalid exchange code for individual delete data", null);
        }
        List<String> processedExchangeCodes = new ArrayList<>();
        if(!records.isEmpty()){
            for(Object record: records){
                String exCode = "";
                if(exchangeCode.equals("111111") && record instanceof ApiBeftnModel){
                    ApiBeftnModel model = (ApiBeftnModel) record;
                    exCode = model.getExchangeCode();
                }   
                else if(exchangeCode.equals("222222") && record instanceof ApiT24Model){
                    ApiT24Model model = (ApiT24Model) record;
                    exCode = model.getExchangeCode();
                }   
                else if(exchangeCode.equals("444444") && record instanceof SwiftModel){
                    SwiftModel model = (SwiftModel) record;
                    exCode = model.getExchangeCode();
                }   
                if(!exCode.isEmpty()){
                    if(!processedExchangeCodes.contains(exCode)){
                        processedExchangeCodes.add(exCode);
                        RepositoryModelWrapper<?> wrapper = repositoryModelMap.get(exCode);
                        if(wrapper != null){
                            Class<?> modelClass = wrapper.getModelClass();
                            resp = customQueryRepository.deleteByFileInfoModelId(modelClass.getSimpleName(), fileInfoModelId);
                        }
                    }else continue;
                }
            }
            return resp;
        }
        return resp;
    }

 
}