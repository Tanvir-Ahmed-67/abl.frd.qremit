package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.helper.ReimbursementModelServiceHelper;
import abl.frd.qremit.converter.model.ReimbursementModel;
import abl.frd.qremit.converter.model.ReportModel;
import abl.frd.qremit.converter.repository.ReimbursementModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    public byte[] loadAllReimbursementForIcashByDate(LocalDate localDate) {
        List<ReimbursementModel> reimbursementModels = findAllCocReimbursementByDate(localDate);
        byte[] in = reimbursementModelServiceHelper.ReimbursementModelsToExcelForIcash(reimbursementModels, localDate);
        return in;
    }
    public Map<String, Object> insertReimbursementData(LocalDate startDate, LocalDate endDate){
        List<ReportModel> reports  = findAllAccountPayeeAndCocPaidDataForReimbursement(startDate, endDate);
        Map<String, Object> resp;
        AtomicInteger counter = new AtomicInteger(1);
        List<ReimbursementModel> allReimbursements = reports.stream()
                .map(report -> {
                    int index = counter.getAndIncrement(); // Get the current count and increment it
                    ReimbursementModel reimbursement = new ReimbursementModel(
                            index,
                            report.getExchangeCode(),
                            report.getTransactionNo(),
                            report.getReportDate(),
                            report.getBeneficiaryName(),
                            report.getBeneficiaryAccount(),
                            report.getRemitterName(),
                            report.getBranchCode(),
                            report.getBranchName(),
                            report.getAmount(),
                            report.getType().equals("2") ? "A/C Payee" : report.getType().equals("4") ? "COC" : "Unknown",
                            endDate
                    );
                    // Calculate and set Govt incentive amounts
                    reimbursement.setGovtIncentiveAmount(
                            reimbursementModelServiceHelper.calculateGovtIncentivePercentage(report.getAmount())
                    );
                    // Calculate and set Agrani incentive amounts
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
        resp = CommonService.getResp(0,"", null);
        resp.put("data", allReimbursements);
        return resp;
    }
    private boolean isNotDuplicate(ReimbursementModel reimbursementModel) {
        return !Boolean.TRUE.equals(reimbursementModelRepository.existsByExchangeCodeAndTransactionNoAndMainAmountAndBeneficiaryAccount(reimbursementModel.getExchangeCode(),reimbursementModel.getTransactionNo(), reimbursementModel.getMainAmount(), reimbursementModel.getBeneficiaryAccount()));
    }
    public List<ReimbursementModel> findAllReimbursementByDate(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllReimbursementByDate(reimbursementDate);
    }
    public List<ReimbursementModel> findAllCocReimbursementByDate(LocalDate reimbursementDate){
        return reimbursementModelRepository.findAllCocReimbursementByDate(reimbursementDate);
    }
    public List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(LocalDate startDate, LocalDate endDate){
        return reimbursementModelRepository.findAllAccountPayeeAndCocPaidDataForReimbursement(startDate, endDate);
    }
}

