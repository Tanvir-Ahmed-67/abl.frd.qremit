package abl.frd.qremit.converter.nafex.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void processTemporaryReport(){
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(currentDate);

        List<OnlineModel> onlineModelList = onlineModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
        List<BeftnModel> beftnModelList = beftnModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
        List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getTemopraryReportData(1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));

        setTemporaryModelData(onlineModelList, "1");
        setTemporaryModelData(accountPayeeModelList, "2");
        setTemporaryModelData(beftnModelList, "3");


    }

    public <T> void setTemporaryModelData(List<T> modelList, String type){
        if(modelList != null && !modelList.isEmpty()){
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
                    //System.out.println(temporaryReportModel);
                    temporaryReportRepository.save(temporaryReportModel);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
