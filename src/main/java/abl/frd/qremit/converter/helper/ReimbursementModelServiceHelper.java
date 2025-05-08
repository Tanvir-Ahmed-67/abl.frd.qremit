package abl.frd.qremit.converter.helper;

import abl.frd.qremit.converter.model.ReimbursementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReimbursementModelServiceHelper {
    @Value("${govt.incentive.percentage}")
    private float govtIncentivePercentage;
    private static float govtIncentivePercentageStatic;
    @Value("${agrani.incentive.percentage}")
    private float agraniIncentivePercentage;
    private static float agraniIncentivePercentageStatic;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Value("${main.account.no.for.reimbursement}")
    private String mainAccountNoForReimbursement;
    private static String mainAccountNoForReimbursementStatic;
    @Value("${govt.incentive.account.no.for.reimbursement}")
    private String govtIncentiveAccountNoForReimbursement;
    private static String govtIncentiveAccountNoForReimbursementStatic;
    @Value("${agrani.incentive.account.no.for.reimbursement}")
    private String agraniIncentiveAccountNoForReimbursement;
    private static String agraniIncentiveAccountNoForReimbursementStatic;
    @PostConstruct
    public void init() {
        govtIncentivePercentageStatic = govtIncentivePercentage;
        agraniIncentivePercentageStatic = agraniIncentivePercentage;
        mainAccountNoForReimbursementStatic = mainAccountNoForReimbursement;
        govtIncentiveAccountNoForReimbursementStatic = govtIncentiveAccountNoForReimbursement;
        agraniIncentiveAccountNoForReimbursementStatic = agraniIncentiveAccountNoForReimbursement;
    }

    @Autowired
    public ReimbursementModelServiceHelper(@Value("${govt.incentive.percentage}") float govtIncentivePercentage, @Value("${agrani.incentive.percentage}") float agraniIncentivePercentage, @Value("12665") String mainAccountNoForReimbursement, @Value("12661") String govtIncentiveAccountNoForReimbursement, @Value("12665") String agraniIncentiveAccountNoForReimbursement) {
        this.govtIncentivePercentage = govtIncentivePercentage;
        this.agraniIncentivePercentage = agraniIncentivePercentage;
        this.mainAccountNoForReimbursement = mainAccountNoForReimbursement;
        this.govtIncentiveAccountNoForReimbursement = govtIncentiveAccountNoForReimbursement;
        this.agraniIncentiveAccountNoForReimbursement = agraniIncentiveAccountNoForReimbursement;
    }
    public static byte[] ReimbursementModelsForGovtIncentiveToExcel(List<ReimbursementModel> reimbursementModelList, LocalDate localDate) {
        StringBuilder csvBuilder = new StringBuilder();
        // Add UTF-8 BOM so Notepad displays special characters correctly
        csvBuilder.append("\uFEFF");
        // Add CSV header
        //csvBuilder.append("S/L,Account No,Branch Code,Branch Name,Amount\n");
        int count = 1;
        // First Portion: mainAmount if non-zero
        for (ReimbursementModel model : reimbursementModelList) {
            if (model.getMainAmount() != 0) {
                csvBuilder.append(count++).append(",")
                        .append(getMainAccountNoForReimbursement().trim()).append(",")
                        .append(model.getBranchCode().trim()).append(",")
                        .append(model.getBranchName().trim()).append(",")
                        .append(model.getMainAmount()).append("\n");
            }
        }
        // Second Portion: govtIncentive if non-zero and type != "4"
        for (ReimbursementModel model : reimbursementModelList) {
            if (!"4".equals(model.getType()) && model.getGovtIncentive() != 0) {
                csvBuilder.append(count++).append(",")
                        .append(getGovtIncentiveAccountNoForReimbursement().trim()).append(",")
                        .append(model.getBranchCode().trim()).append(",")
                        .append(model.getBranchName().trim()).append(",")
                        .append(model.getGovtIncentive()).append("\n");
            }
        }
        // Return as UTF-8 encoded byte array
        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] ReimbursementModelsToExcelForCocClaim(List<ReimbursementModel> reimbursementModelList, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder csvBuilder = new StringBuilder();
        // Add UTF-8 BOM so Notepad can read special characters
        csvBuilder.append("\uFEFF");
        // Header row
        csvBuilder.append("BR CODE,REF NO,REMITTANCE,INCENTIVE,DATE,EX CODE,CONTACT NO,REMITTER,BENEFICIARY\n");
        // Data rows
        for (ReimbursementModel model : reimbursementModelList) {
            if (model.getMainAmount() != 0) {
                csvBuilder.append(model.getBranchCode().trim()).append(",")
                        .append(model.getTransactionNo().trim()).append(",")
                        .append(model.getMainAmount()).append(",")
                        .append(model.getGovtIncentive()).append(",")
                        .append(model.getReimbursementDate().format(formatter)).append(",")
                        .append(model.getExchangeCode().trim()).append(",")
                        .append(model.getBeneficiaryAccount().trim()).append(",")
                        .append(model.getRemitterName().trim()).append(",")
                        .append(model.getBeneficiaryName().trim()).append("\n");
            }
        }
        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] ReimbursementModelsForAgraniIncentiveToExcel(List<ReimbursementModel> reimbursementModelList, LocalDate localDate) {
        StringBuilder csvBuilder = new StringBuilder();
        // Add UTF-8 BOM so Notepad displays Unicode characters correctly
        csvBuilder.append("\uFEFF");
        // Add CSV header
        //csvBuilder.append("S/L,Account No,Branch Code,Branch Name,Amount\n");
        int count = 1;
        // Fill cell 4 with agraniIncentiveAmount if non-zero and type != "4"
        for (ReimbursementModel model : reimbursementModelList) {
            if (!"4".equals(model.getType()) && model.getAgraniIncentive() != 0) {
                csvBuilder.append(count++).append(",")
                        .append(getAgraniIncentiveAccountNoForReimbursement().trim()).append(",")
                        .append(model.getBranchCode().trim()).append(",")
                        .append(model.getBranchName().trim()).append(",")
                        .append(model.getAgraniIncentive()).append("\n");
            }
        }
        // Return UTF-8 encoded CSV
        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
    public static String getMainAccountNoForReimbursement() {
        return mainAccountNoForReimbursementStatic;
    }

    public static void setMainAccountNoForReimbursement(String mainAccountNoForReimbursement) {
        ReimbursementModelServiceHelper.mainAccountNoForReimbursementStatic = mainAccountNoForReimbursement;
    }

    public static String getGovtIncentiveAccountNoForReimbursement() {
        return govtIncentiveAccountNoForReimbursementStatic;
    }

    public static void setGovtIncentiveAccountNoForReimbursement(String govtIncentiveAccountNoForReimbursement) {
        ReimbursementModelServiceHelper.govtIncentiveAccountNoForReimbursementStatic = govtIncentiveAccountNoForReimbursement;
    }

    public static String getAgraniIncentiveAccountNoForReimbursement() {
        return agraniIncentiveAccountNoForReimbursementStatic;
    }

    public float getGovtIncentivePercentage() {
        return this.govtIncentivePercentage;
    }

    public float getAgraniIncentivePercentage() {
        return this.agraniIncentivePercentage;
    }

    public void setAgraniIncentivePercentage(float agraniIncentivePercentage) {
        this.agraniIncentivePercentageStatic = agraniIncentivePercentage;
    }

    public static Double calculateGovtIncentivePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (govtIncentivePercentageStatic / 100f) * mainAmount;
        return Double.valueOf(df.format(percentage));
    }
    public static Double calculateAgraniIncentivePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (agraniIncentivePercentageStatic / 100f) * mainAmount;
        return Double.valueOf(df.format(percentage));
    }
}
