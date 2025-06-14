package abl.frd.qremit.converter.service;
import java.util.*;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import abl.frd.qremit.converter.repository.LogModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.repository.ErrorDataModelRepository;

@Service
public class ErrorDataModelService {
    @Autowired
    ErrorDataModelRepository errorDataModelRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    LogModelRepository logModelRepository;
    @Autowired
    LogModelService logModelService;
    @Autowired
    CommonService commonService;

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
    
    public List<ErrorDataModel> findUserModelListByExchangeCodeAndUpdateStatus(String exchangeCode, int updateStatus, int fileInfoModelId){
        List<String> exchangeCodeList = Arrays.asList(exchangeCode.split(","));
        if(fileInfoModelId != 0){
            return errorDataModelRepository.findErrorByExchangeCodeAndFileId(exchangeCodeList, updateStatus, fileInfoModelId);
        }else return errorDataModelRepository.findErrorByExchangeCode(exchangeCodeList, updateStatus);
    }

    //find errorDataModel 
    public ErrorDataModel findErrorModelById(int id){
        return errorDataModelRepository.findById(id);
    }

    // status 0- pending, 1- updated by user, 2- approve, 3- deny
    public void updateErrorDataModelUpdateStatus(int id, int updateStatus){
        errorDataModelRepository.updateUpdateStatusById(id, updateStatus);
    }

    public Map<String, Object> deleteErrorDataById(int id, HttpServletRequest request, int userId){
        Map<String, Object> resp = new HashMap<>();
        ErrorDataModel errorDataModel = errorDataModelRepository.findById(id);
        if(errorDataModel != null){
            int fileInfoModelId = errorDataModel.getFileInfoModel().getId();
            String exchangeCode = errorDataModel.getExchangeCode();
            errorDataModelRepository.deleteById(id);
            Map<String, Object> info = new HashMap<>();
            info.put("errorDataModel", errorDataModel);
            Map<String, Object> logResp = logModelService.addLogModel(userId, fileInfoModelId, exchangeCode, String.valueOf(id), "2", info, request);
            if((Integer) logResp.get("err") == 1)   return logResp;
            resp = CommonService.getResp(0, "Information Deleted", null);
            resp.put("fileInfoModelId", fileInfoModelId);
        }else resp = CommonService.getResp(1, "No Data Found", null);
        return resp;
    }

    public Map<String, Object> processUpdateErrorDataById(@RequestParam Map<String, String> formData, HttpServletRequest request, int userId){
        Map<String, Object> resp = new HashMap<>();
        String exchangeCode = formData.get("exchangeCode");
        String id = formData.get("id");
        ErrorDataModel errorDataModel = findErrorModelById(CommonService.convertStringToInt(id));
        entityManager.detach(errorDataModel); // Detach ErrorDataModel to prevent auto-updates
        if(errorDataModel == null)  return CommonService.getResp(1, "No data found following Error Model", null);
        if(errorDataModel.getUpdateStatus() != 0)   return CommonService.getResp(1, "Invalid Type for update data", null);  //for update status must be 0
        int fileInfoModelId = errorDataModel.getFileInfoModel().getId();

        Map<String, Object> errorDataMap = getErrorDataModelMap(errorDataModel); 

        Map<String, Object> info = new HashMap<>();
        info.put("oldData", errorDataMap);
        String bankName = formData.get("bankName").trim();
        String beneficiaryAccount =formData.get("beneficiaryAccount").trim();
        String beneficiaryName = formData.get("beneficiaryName").trim();
        String branchCode = formData.get("branchCode").trim();
        String amount = formData.get("amount").toString();

        String errorMessage = "";
        //check amount and reference number same
        if(!errorDataModel.getAmount().equals(Double.valueOf(amount)) || !errorDataModel.getTransactionNo().equals(formData.get("transactionNo"))){
            errorMessage = "Amount or Transaction No mismatched";
            return CommonService.getResp(1, errorMessage, null);
        }
        errorMessage = CommonService.getErrorMessage(beneficiaryAccount, beneficiaryName, amount, bankName, branchCode);
        if(!errorMessage.isEmpty()) return CommonService.getResp(1, errorMessage, null);
        
        String typeFlag = CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode);
        errorDataModel.setBankCode(formData.get("bankCode"));
        errorDataModel.setBankName(bankName);
        errorDataModel.setBranchCode(branchCode);
        errorDataModel.setBranchName(formData.get("branchName"));
        errorDataModel.setBeneficiaryAccount(beneficiaryAccount);
        errorDataModel.setBeneficiaryName(beneficiaryName);
        errorDataModel.setTypeFlag(typeFlag);

        if(("2").equals(typeFlag)){
            Map<String, Object> routingMap = commonService.checkAblBranchCode(branchCode);
            if((Integer) routingMap.get("err") == 1)    return CommonService.getResp(1, "Invalid Branch Code for A/C Payee", null);
        }
        
        Map<String, Object> updatedData = getErrorDataModelMap(errorDataModel);
        updatedData.put("userId", userId);
        info.put("updatedData", updatedData);
        int errorDataModelId = errorDataModel.getId();
        resp = logModelService.addLogModel(userId, fileInfoModelId, exchangeCode, String.valueOf(errorDataModelId), "1", info, request);
        if((Integer) resp.get("err") == 0){
            try{
                updateErrorDataModelUpdateStatus(errorDataModelId, 1);
                resp = CommonService.getResp(0, "Data updated", null);
            }catch(Exception e){
                resp = CommonService.getResp(1, "Error Updating status: " + e.getMessage(), null);
            }
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
        resp.put("uploadDateTime", uploadDateTime);
        resp.put("typeFlag", errorDataModel.getTypeFlag());
        resp.put("userId", errorDataModel.getUserModel().getId());
        resp.put("fileInfoId", errorDataModel.getFileInfoModel().getId());
        return resp;
    }

    public List<Map<String, Object>> getErrorReport(int userId, int fileInfoModelId, String exchangeCode){
        //List<ErrorDataModel> errorDataModel = findUserModelListByIdAndUpdateStatus(userId, 0, fileInfoModelId);
        List<ErrorDataModel> errorDataModel = findUserModelListByExchangeCodeAndUpdateStatus(exchangeCode, 0, fileInfoModelId);
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
            dataMap.put("branchCode", emodel.getBranchCode());
            dataMap.put("beneficiaryName", emodel.getBeneficiaryName());
            dataMap.put("beneficiaryAccountNo", emodel.getBeneficiaryAccount());
            dataMap.put("transactionNo", emodel.getTransactionNo());
            dataMap.put("amount", emodel.getAmount());
            dataMap.put("uploadDateTime", CommonService.convertDateToString(emodel.getUploadDateTime()));
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
            List<Map<String, Object>> logData =  logModelService.findLogModelByDataId(errorDataId);
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
            action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round view_error", errorDataId,"View");
            
            dataMap.put("sl", sl++);
            dataMap.put("bankName", updatedDataMap.get("bankName"));
            dataMap.put("branchName", updatedDataMap.get("branchName"));
            dataMap.put("branchCode", updatedDataMap.get("branchCode"));
            dataMap.put("beneficiaryName", updatedDataMap.get("beneficiaryName"));
            dataMap.put("beneficiaryAccountNo", updatedDataMap.get("beneficiaryAccount"));
            dataMap.put("transactionNo", updatedDataMap.get("transactionNo"));
            dataMap.put("amount", updatedDataMap.get("amount"));
            dataMap.put("exchangeCode", emodel.getExchangeCode());
            dataMap.put("uploadDateTime", CommonService.convertDateToString(emodel.getUploadDateTime()));
            dataMap.put("errorMessage", emodel.getErrorMessage());
            dataMap.put("action", action);
            dataList.add(dataMap);
        }
        return dataList;
    }

    public Map<String, Object> saveErrorModelList(List<ErrorDataModel> errorDataModelList){
        Map<String, Object> resp = new HashMap<>();
        int errorCount = 0;
        if (!errorDataModelList.isEmpty()) {
            List<String> transactionList = new ArrayList<>();
            for(ErrorDataModel model: errorDataModelList){
                transactionList.add(model.getTransactionNo());
            }
            List<ErrorDataModel> existingDataList = errorDataModelRepository.findByTransactionNoIn(transactionList);
            Set<String> existingTransactionNos = new HashSet<>();
            for(ErrorDataModel data: existingDataList){
                existingTransactionNos.add(data.getTransactionNo());
            }
            errorDataModelList.removeIf(model -> existingTransactionNos.contains(model.getTransactionNo()));
            if(errorDataModelList.isEmpty()){
                resp.put("errorCount", errorCount);
                return resp;
            }    
            try{
                List<ErrorDataModel> errorDataModels = errorDataModelRepository.saveAll(errorDataModelList);
                errorCount = errorDataModels.size();
                resp.put("errorCount", errorCount);
            }catch(Exception e){
                resp.put("errorMessage", e.getMessage());
            }
        }
        return resp;
    }
}
