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
import java.util.stream.Collectors;

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
        List<ReportModel> reports  = findAllAccountPayeeAndCocPaidDataForReimbursement(localDate);
        List<ReimbursementModel> allReimbursements = reports.stream()
                .map(report -> {
                    ReimbursementModel reimbursement = new ReimbursementModel(
                            report.getExchangeCode(),
                            report.getTransactionNo(),
                            report.getReportDate(),
                            report.getBeneficiaryName(),
                            report.getBeneficiaryAccount(),
                            report.getRemitterName(),
                            report.getBranchCode(),
                            report.getBranchName(),
                            report.getAmount()
                    );
                    // Calculate and set incentive amounts
                    reimbursement.setGovtIncentiveAmount(
                            reimbursementModelServiceHelper.calculateGovtIncentivePercentage(report.getAmount())
                    );
                    reimbursement.setAgraniIncentiveAmount(
                            reimbursementModelServiceHelper.calculateAgraniIncentivePercentage(report.getAmount())
                    );
                    return reimbursement;
                })
                .collect(Collectors.toList());
        // Filter out duplicates and save the non-duplicate entries
        List<ReimbursementModel> nonDuplicateReimbursements = allReimbursements.stream()
                .filter(this::isNotDuplicate)
                .collect(Collectors.toList());
        reimbursementModelRepository.saveAll(nonDuplicateReimbursements);
        // Return the complete list of reimbursements, including duplicates
        return allReimbursements;
    }
    private boolean isNotDuplicate(ReimbursementModel reimbursementModel) {
        return !Boolean.TRUE.equals(reimbursementModelRepository.existsByExchangeCodeAndTransactionNoAndMainAmountAndBeneficiaryAccount(reimbursementModel.getExchangeCode(),reimbursementModel.getTransactionNo(), reimbursementModel.getMainAmount(), reimbursementModel.getBeneficiaryAccount()));
    }
    public List<ReimbursementModel> findAllReimbursementByDate(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllReimbursementByDate(reimbursementDate);
    }
    public List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllAccountPayeeAndCocPaidDataForReimbursement(reimbursementDate);
    }
}

