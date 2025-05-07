package abl.frd.qremit.converter.helper;

import abl.frd.qremit.converter.model.AccountPayeeModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@Component
public class AccountPayeeModelServiceHelper {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Value("${govt.incentive.percentage}")
    private float govtIncentivePercentage;
    private static float govtIncentivePercentageStatic;
    @Value("${agrani.incentive.percentage}")
    private float agraniIncentivePercentage;
    private static float agraniIncentivePercentageStatic;
    @PostConstruct
    public void init() {
        govtIncentivePercentageStatic = govtIncentivePercentage;
        agraniIncentivePercentageStatic = agraniIncentivePercentage;
    }

    public static ByteArrayInputStream AccountPayeeModelToCSV(List<AccountPayeeModel> accountPayeeModelList) {
        final CSVFormat format = CSVFormat.DEFAULT;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (AccountPayeeModel accountPayeeModel : accountPayeeModelList) {
                List<Object> data = Arrays.asList(
                        accountPayeeModel.getTransactionNo().trim(),
                        accountPayeeModel.getCreditMark().trim(),
                        accountPayeeModel.getEnteredDate().trim(),
                        accountPayeeModel.getCurrency().trim(),
                        accountPayeeModel.getAmount(),
                        accountPayeeModel.getRemitterName().trim().replace(",",""),
                        accountPayeeModel.getExchangeCode().trim(),
                        accountPayeeModel.getBankName().trim().replace(",",""),
                        accountPayeeModel.getBranchName().trim().replace(",",""),
                        "NULL",
                        accountPayeeModel.getBeneficiaryAccount().trim(),
                        accountPayeeModel.getBeneficiaryName().trim().replace(",",""),
                        "NULL",
                        "NULL",
                        accountPayeeModel.getBranchCode(),
                        "NULL",
                        "NULL",
                        "NULL",
                        "NULL",
                        accountPayeeModel.getIncentive(), //incentive,
                        "15"
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to Download " + e.getMessage());
        }
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
