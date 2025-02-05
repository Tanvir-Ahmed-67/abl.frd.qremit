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

    public byte[] loadAllReimbursementByDate(LocalDate fromDate, LocalDate toDate) {
        List<ReimbursementModel> reimbursementModels = findAllReimbursementByDate(fromDate, toDate);
        byte[] in = ReimbursementModelServiceHelper.ReimbursementModelsToExcel(reimbursementModels, toDate);
        return in;
    }
    public byte[] loadAllReimbursementForIcashByDate(LocalDate fromDate, LocalDate toDate) {
        List<ReimbursementModel> reimbursementModels = findAllCocReimbursementByDate(fromDate, toDate);
        byte[] in = ReimbursementModelServiceHelper.ReimbursementModelsToExcelForIcash(reimbursementModels, toDate);
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
                            report.getType(),
                            endDate
                    );
                    // Calculate and set Govt incentive amounts
                    reimbursement.setGovtIncentiveAmount(
                            ReimbursementModelServiceHelper.calculateGovtIncentivePercentage(report.getAmount())
                    );
                    // Calculate and set Agrani incentive amounts
                    reimbursement.setAgraniIncentiveAmount(
                            ReimbursementModelServiceHelper.calculateAgraniIncentivePercentage(report.getAmount())
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
    public List<ReimbursementModel> findAllReimbursementByDate(LocalDate fromDate, LocalDate toDate){
        return reimbursementModelRepository.findAllReimbursementByDate(fromDate, toDate);
    }
    public List<ReimbursementModel> findAllCocReimbursementByDate(LocalDate fromDate, LocalDate toDate){
        return reimbursementModelRepository.findAllCocReimbursementByDate(fromDate, toDate);
    }
    public List<ReportModel> findAllAccountPayeeAndCocPaidDataForReimbursement(LocalDate startDate, LocalDate endDate){
        return reimbursementModelRepository.findAllAccountPayeeAndCocPaidDataForReimbursement(startDate, endDate);
    }
}

