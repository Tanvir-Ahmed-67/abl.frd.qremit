package abl.frd.qremit.converter.nafex.service;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.LogModel;
import abl.frd.qremit.converter.nafex.repository.ErrorDataModelRepository;
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;

@Service
public class ErrorDataModelService {
    @Autowired
    ErrorDataModelRepository errorDataModelRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    LogModelRepository logModelRepository;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    @Autowired
    LogModelService logModelService;

    //find errorDataModel by userID
    public List<ErrorDataModel> findUserModelListById(int userId){
        return errorDataModelRepository.findByUserModelId(userId);
    }

    //find errorDataModel by updateStatus
    public List<ErrorDataModel> findUserModelListByUpdateStatus(int updateStatus){
        return errorDataModelRepository.findByUpdateStatus(updateStatus);
    }

    //find errorDataModel by using userId and updateStatus
    public List<ErrorDataModel> findUserModelListByIdAndUpdateStatus(int userId, int updateStatus, int fileInfoModelId){
        if(fileInfoModelId != 0){
            return errorDataModelRepository.findByUserModelIdAndUpdateStatusAndFileInfoModelId(userId, updateStatus, fileInfoModelId);
        }else return errorDataModelRepository.findByUserModelIdAndUpdateStatus(userId, updateStatus);
    } 

    //find errorDataModel 
    public ErrorDataModel findErrorModelById(int id){
        return errorDataModelRepository.findById(id);
    }

    // status 0- pending, 1- updated by user, 2- approve, 3- deny
    public void updateErrorDataModelUpdateStatus(int id, int updateStatus){
        errorDataModelRepository.updateUpdateStatusById(id, updateStatus);
    }

    public void deleteErrorDataById(int id){
        errorDataModelRepository.deleteById(id);
    }

    public Map<String, Object> processUpdateErrorDataById(@RequestParam Map<String, String> formData, HttpServletRequest request){
        Map<String, Object> resp = new HashMap<>();
        String exchangeCode = formData.get("exchangeCode");
        String id = formData.get("id");
        ErrorDataModel errorDataModel = findErrorModelById(Integer.parseInt(id));
        entityManager.detach(errorDataModel); // Detach ErrorDataModel to prevent auto-updates
        if(errorDataModel == null)  return CommonService.getResp(1, "No data found following Error Model", null);
        if(errorDataModel.getUpdateStatus() != 0)   return CommonService.getResp(1, "Invalid Type for update data", null);  //for update status must be 0

        Map<String, Object> errorDataMap = getErrorDataModelMap(errorDataModel); 
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        String tbl = CommonService.getBaseTableName(exchangeHouseModel.getBaseTableName());
        Map<String, Object> info = new HashMap<>();
        info.put("oldData", errorDataMap);
        String ipAddress = request.getRemoteAddr();
        String bankName = formData.get("bankName").trim();
        String beneficiaryAccount =formData.get("beneficiaryAccount").trim();
        String branchCode = formData.get("branchCode").trim();

        String errorMessage = "";
        //check amount and reference number same
        if(!errorDataModel.getAmount().equals(Double.valueOf(formData.get("amount"))) || !errorDataModel.getTransactionNo().equals(formData.get("transactionNo"))){
            errorMessage = "Amount or Transaction No mismatched";
            return CommonService.getResp(1, errorMessage, null);
        }
        //check empty 
        errorMessage = CommonService.checkBeneficiaryNameOrAmountOrBeneficiaryAccount(formData.get("beneficiaryAccount"), formData.get("beneficiaryName"), formData.get("amount"));
        if(!errorMessage.isEmpty()) return CommonService.getResp(1, errorMessage, null);

        if(CommonService.isBeftnFound(formData.get("bankName"), formData.get("beneficiaryAccount"), formData.get("branchCode"))){
            errorMessage = CommonService.checkBEFTNRouting(formData.get("branchCode"));
        }else if(CommonService.isCocFound(formData.get("beneficiaryAccount"))){
            errorMessage = CommonService.checkCOCBankName(formData.get("bankName"));
        }else if(CommonService.isAccountPayeeFound(formData.get("bankName"), formData.get("beneficiaryAccount"), formData.get("branchCode"))){
            errorMessage = CommonService.checkABLAccountAndRoutingNo(formData.get("beneficiaryAccount"), formData.get("branchCode"), formData.get("bankName"));
            if(errorMessage.isEmpty()) errorMessage = CommonService.checkCOString(formData.get("beneficiaryAccount"));
        }else if(CommonService.isOnlineAccoutNumberFound(formData.get("beneficiaryAccount"))){

        }
        if(!errorMessage.isEmpty()) return CommonService.getResp(1, errorMessage, null);

        errorDataModel.setBankCode(formData.get("bankCode"));
        errorDataModel.setBankName(formData.get("bankName"));
        errorDataModel.setBranchCode(formData.get("branchCode"));
        errorDataModel.setBranchName(formData.get("branchName"));
        errorDataModel.setBeneficiaryAccount(formData.get("beneficiaryAccount"));
        errorDataModel.setBeneficiaryName(formData.get("beneficiaryName"));
        errorDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
        
        Map<String, Object> updatedData = getErrorDataModelMap(errorDataModel);
        info.put("updatedData", updatedData);
        String infoStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try{
            infoStr = objectMapper.writeValueAsString(info);
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
        String userId = String.valueOf(errorDataModel.getUserModel().getId());
        LogModel logModel = new LogModel(userId, String.valueOf(errorDataModel.getId()), exchangeCode, "1", infoStr, ipAddress);
        LogModel saveLogModel = logModelRepository.save(logModel);
        if(saveLogModel != null){
            try{
                updateErrorDataModelUpdateStatus(errorDataModel.getId(), 1);
                resp = CommonService.getResp(0, "Data updated", null);
            }catch(Exception e){
                resp = CommonService.getResp(1, "Error Updating status: " + e.getMessage(), null);
            }
        }else{
            resp = CommonService.getResp(1, "Error Updating Information", null);
        }
        return resp;
    }

    public Map<String, Object> getErrorDataModelMap(ErrorDataModel errorDataModel){
        Map<String, Object> resp = new HashMap<>();
        String uploadDateTime = CommonService.convertDateToString(errorDataModel.getUploadDateTime());
        resp.put("id", errorDataModel.getId());
        resp.put("exchangeCode", errorDataModel.getExchangeCode());
        resp.put("transactionNo", errorDataModel.getTransactionNo());
        resp.put("currency", errorDataModel.getCurrency());
        resp.put("amount", errorDataModel.getAmount());
        resp.put("enteredDate", errorDataModel.getEnteredDate());
        resp.put("remitterName", errorDataModel.getRemitterName());
        resp.put("remitterMobile", errorDataModel.getRemitterMobile());
        resp.put("beneficiaryName", errorDataModel.getBeneficiaryName());
        resp.put("beneficiaryAccount", errorDataModel.getBeneficiaryAccount());
        resp.put("beneficiaryMobile", errorDataModel.getBeneficiaryMobile());
        resp.put("bankName", errorDataModel.getBankName());
        resp.put("bankCode", errorDataModel.getBankCode());
        resp.put("branchName", errorDataModel.getBranchName());
        resp.put("branchCode", errorDataModel.getBranchCode());
        resp.put("draweeBranchName", errorDataModel.getDraweeBranchName());
        resp.put("draweeBranchCode", errorDataModel.getDraweeBranchCode());
        resp.put("purposeOfRemittance", errorDataModel.getPurposeOfRemittance());
        resp.put("sourceOfIncome", errorDataModel.getSourceOfIncome());
        resp.put("processFlag", errorDataModel.getProcessFlag());
        resp.put("typeFlag", errorDataModel.getTypeFlag());
        resp.put("processedBy", errorDataModel.getProcessedBy());
        resp.put("processedDate", errorDataModel.getProcessedDate());
        resp.put("errorMessage", errorDataModel.getErrorMessage());
        //resp.put("uploadDateTime", errorDataModel.getUploadDateTime());
        resp.put("uploadDateTime", uploadDateTime);
        resp.put("typeFlag", errorDataModel.getTypeFlag());
        resp.put("userId", errorDataModel.getUserModel().getId());
        resp.put("fileInfoId", errorDataModel.getFileInfoModel().getId());
        return resp;
    }

    public List<Map<String, Object>> getErrorReport(int userId, int fileInfoModelId){
        List<ErrorDataModel> errorDataModel = findUserModelListByIdAndUpdateStatus(userId, 0, fileInfoModelId);
        int sl = 1;
        String action = "";
        String btn = "";
        List<Map<String, Object>> dataList = new ArrayList<>();
        for(ErrorDataModel emodel: errorDataModel){
            Map<String, Object> dataMap = new HashMap<>();
            btn = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm edit_error",String.valueOf(emodel.getId()),"Edit");
            btn += CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm delete_error",String.valueOf(emodel.getId()),"Delete");
            action = CommonService.generateTemplateBtn("template-btngroup.txt", "#", "", "", btn);

            dataMap.put("sl", sl++);
            dataMap.put("bankName", emodel.getBankName());
            dataMap.put("branchName", emodel.getBranchName());
            dataMap.put("routingNo", emodel.getBranchCode());
            dataMap.put("beneficiaryName", emodel.getBeneficiaryName());
            dataMap.put("beneficiaryAccountNo", emodel.getBeneficiaryAccount());
            dataMap.put("transactionNo", emodel.getTransactionNo());
            dataMap.put("amount", emodel.getAmount());
            dataMap.put("exchangeCode", emodel.getExchangeCode());
            dataMap.put("errorMessage", emodel.getErrorMessage());
            dataMap.put("action", action);
            dataList.add(dataMap);
        }
        return dataList;
    }

    public List<Map<String, Object>> getErrorUpdateReport(){
        List<ErrorDataModel> errorDataModel = findUserModelListByUpdateStatus(1);
        List<Map<String, Object>> dataList = new ArrayList<>();
        int sl = 1;
        String action = "";
        for(ErrorDataModel emodel: errorDataModel){
            String errorDataId = String.valueOf(emodel.getId());
            List<Map<String, Object>> logData =  logModelService.findLogModelByErrorDataId(errorDataId);
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
            action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round view_error", errorDataId,"View");
            
            dataMap.put("sl", sl++);
            dataMap.put("bankName", updatedDataMap.get("bankName"));
            dataMap.put("branchName", updatedDataMap.get("branchName"));
            dataMap.put("routingNo", updatedDataMap.get("branchCode"));
            dataMap.put("beneficiaryName", updatedDataMap.get("beneficiaryName"));
            dataMap.put("beneficiaryAccountNo", updatedDataMap.get("beneficiaryAccount"));
            dataMap.put("transactionNo", updatedDataMap.get("transactionNo"));
            dataMap.put("amount", updatedDataMap.get("amount"));
            dataMap.put("exchangeCode", emodel.getExchangeCode());
            dataMap.put("errorMessage", emodel.getErrorMessage());
            dataMap.put("action", action);
            dataList.add(dataMap);
        }
        return dataList;
    }

    public Map<String, Object> saveErrorModelList(List<ErrorDataModel> errorDataModelList){
        Map<String, Object> resp = new HashMap<>();
        if (!errorDataModelList.isEmpty()) {
            try{
                List<ErrorDataModel> errorDataModels = errorDataModelRepository.saveAll(errorDataModelList);
                int errorCount = errorDataModels.size();
                resp.put("errorCount", errorCount);
            }catch(Exception e){
                resp.put("errorMessage", e.getMessage());
            }
        }
        return resp;
    }
}
