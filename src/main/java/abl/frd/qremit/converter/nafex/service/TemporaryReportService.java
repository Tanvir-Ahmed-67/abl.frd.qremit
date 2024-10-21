package abl.frd.qremit.converter.nafex.service;

import java.time.LocalDateTime;
import java.util.*;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.BeftnModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import abl.frd.qremit.converter.nafex.model.TemporaryReportModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.TemporaryReportRepository;

@Service
public class TemporaryReportService {
    @Autowired
    TemporaryReportRepository temporaryReportRepository;
    @Autowired
    OnlineModelService onlineModelService;
    @Autowired
    BeftnModelService beftnModelService;
    @Autowired
    AccountPayeeModelService accountPayeeModelService;

    public Map<String, Object> processTemporaryReport(){
        Map<String, Object> resp = new HashMap<>();
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(currentDate);

        List<OnlineModel> onlineModelList = onlineModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
        List<BeftnModel> beftnModelList = beftnModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
        List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
        
        resp = setTemporaryModelData(onlineModelList, "1");
        int count = resp.size();
        if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
        resp = setTemporaryModelData(accountPayeeModelList, "2");
        count += resp.size();
        if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
        resp = setTemporaryModelData(beftnModelList, "3");
        count += resp.size();
        if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
        if(count == 0)  return CommonService.getResp(0, "No data found for processing temporary table", null);
        resp = CommonService.getResp(0, "Data Processed temporary report successfully", null);
        return resp;
    }

    

    public <T> Map<String, Object> setTemporaryModelData(List<T> modelList, String type){
        Map<String, Object> resp = new HashMap<>();
        //System.out.println(modelList);
        if(modelList != null && !modelList.isEmpty()){
            int count = 0;
            for(T model: modelList){
                TemporaryReportModel temporaryReportModel = new TemporaryReportModel();
                try{
                    String transactionNo = (String) CommonService.getPropertyValue(model, "getTransactionNo");
                    String exchangeCode = (String) CommonService.getPropertyValue(model, "getExchangeCode");
                    Double amount = (Double) CommonService.getPropertyValue(model, "getAmount");
                    Optional<TemporaryReportModel> temporaryReport = temporaryReportRepository.findByExchangeCodeAndTransactionNoAndAmount(exchangeCode, transactionNo, amount);
                    if(temporaryReport.isPresent()) continue;

                    String branchMethod = (("3").matches(type)) ? "getRoutingNo": "getBranchCode";
                    temporaryReportModel.setExchangeCode(exchangeCode);
                    temporaryReportModel.setTransactionNo(transactionNo);
                    temporaryReportModel.setBankCode((String) CommonService.getPropertyValue(model, "getBankCode"));
                    temporaryReportModel.setBankName((String) CommonService.getPropertyValue(model, "getBankName"));
                    temporaryReportModel.setBranchCode((String) CommonService.getPropertyValue(model, branchMethod));
                    temporaryReportModel.setBranchName((String) CommonService.getPropertyValue(model, "getBranchName"));
                    temporaryReportModel.setAmount(amount);
                    temporaryReportModel.setBeneficiaryName((String) CommonService.getPropertyValue(model, "getBeneficiaryName"));
                    temporaryReportModel.setBeneficiaryAccount((String) CommonService.getPropertyValue(model, "getBeneficiaryAccount"));
                    temporaryReportModel.setIncentive((Double) CommonService.getPropertyValue(model, "getIncentive"));
                    temporaryReportModel.setRemitterName((String) CommonService.getPropertyValue(model, "getRemitterName"));
                    temporaryReportModel.setDownloadDateTime((LocalDateTime) CommonService.getPropertyValue(model, "getDownloadDateTime"));
                    temporaryReportModel.setUploadDateTime((LocalDateTime) CommonService.getPropertyValue(model, "getUploadDateTime"));
                    User user = (User) CommonService.getPropertyValue(model, "getUserModel");
                    temporaryReportModel.setUploadUserId((int) user.getId());
                    FileInfoModel fileInfoModel= (FileInfoModel) CommonService.getPropertyValue(model, "getFileInfoModel");
                    temporaryReportModel.setFileInfoModelId((int) fileInfoModel.getId());
                    temporaryReportModel.setType(type);
                    temporaryReportModel.setDataModelId((int) CommonService.getPropertyValue(model, "getId"));
                    //System.out.println(temporaryReportModel);
                    temporaryReportRepository.save(temporaryReportModel);
                    count++;
                }catch(Exception e){
                    e.printStackTrace();
                    return CommonService.getResp(1, "Error processing model " + e.getMessage(), null);
                }
            }
            if(count == 0)  return CommonService.getResp(0, "No data found for processing temporary table", null);
            resp = CommonService.getResp(0, "Data processed temporary report successfully", null);
        }
        return resp;
    }

    @Transactional
    public void truncateTemporaryReportModel(){
        temporaryReportRepository.truncateTemporaryReport();
    }
     
}
