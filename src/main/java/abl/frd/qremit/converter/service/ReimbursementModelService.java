package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.helper.ReimbursementModelServiceHelper;
import abl.frd.qremit.converter.model.ReimbursementModel;
import abl.frd.qremit.converter.model.ReportModel;
import abl.frd.qremit.converter.repository.ReimbursementModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReimbursementModelService {
    @Autowired
    ReimbursementModelRepository reimbursementModelRepository;
    @Autowired
    ReportService reportService;
    @Autowired
    CommonService commonService;
    @Autowired
    ReimbursementModelServiceHelper reimbursementModelServiceHelper;

    public byte[] loadAllReimbursementByDate(LocalDate localDate) {
        List<ReimbursementModel> reimbursementModels = findAllReimbursementByDate(localDate);
        byte[] in = reimbursementModelServiceHelper.ReimbursementModelsToExcel(reimbursementModels, localDate);
        return in;
    }
    public List<ReimbursementModel> insertReimbursementData(LocalDate localDate){
        List<ReportModel> reportModelList = findAllAccountPayeeAndCocPaidDataForReimbursement(localDate);
        List<ReimbursementModel> reimbursementModelList = new ArrayList<>();
        if (reportModelList == null || reportModelList.isEmpty()) {
            return reimbursementModelList; // Return an empty list if input is null or empty
        }
        for (ReportModel reportModel : reportModelList) {
            ReimbursementModel reimbursementModel = new ReimbursementModel();
            reimbursementModel.setReimbursementDate(localDate);
            reimbursementModel.setTransactionNo(reportModel.getTransactionNo());
            reimbursementModel.setExchangeCode(reportModel.getExchangeCode());
            reimbursementModel.setBeneficiaryName(reportModel.getBeneficiaryName());
            reimbursementModel.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
            reimbursementModel.setRemitterName(reportModel.getRemitterName());
            reimbursementModel.setBranchCode(reportModel.getBranchCode());
            reimbursementModel.setBranchName(reportModel.getBranchName());
            reimbursementModel.setMainAmount(reportModel.getAmount());
            reimbursementModel.setGovtIncentiveAmount(reimbursementModelServiceHelper.calculateGovtIncentivePercentage(reportModel.getAmount()));
            reimbursementModel.setAgraniIncentiveAmount(reimbursementModelServiceHelper.calculateAgraniIncentivePercentage(reportModel.getAmount()));

            reimbursementModelList.add(reimbursementModel);
        }
        return reimbursementModelRepository.saveAll(reimbursementModelList);
    }

    public List<ReimbursementModel> findAllReimbursementByDate(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllReimbursementByDate(reimbursementDate);
    }
    public List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllAccountPayeeAndCocPaidDataForReimbursement(reimbursementDate);
    }
}

