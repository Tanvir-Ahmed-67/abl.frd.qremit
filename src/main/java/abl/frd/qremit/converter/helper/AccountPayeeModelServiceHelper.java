package abl.frd.qremit.converter.helper;

import abl.frd.qremit.converter.model.AccountPayeeModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private static float incentivePercentage;
    @Autowired
    public AccountPayeeModelServiceHelper(@Value("${incentive.percentage}") float incentivePercentage) {
        this.incentivePercentage = incentivePercentage;
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
                        accountPayeeModel.getRemitterName().trim(),
                        accountPayeeModel.getExchangeCode().trim(),
                        accountPayeeModel.getBankName().trim(),
                        accountPayeeModel.getBranchName().trim(),
                        "NULL",
                        accountPayeeModel.getBeneficiaryAccount().trim(),
                        accountPayeeModel.getBeneficiaryName().trim(),
                        "NULL",
                        "NULL",
                        accountPayeeModel.getBranchCode(),
                        "NULL",
                        "NULL",
                        "NULL",
                        "NULL",
                        calculatePercentage(accountPayeeModel.getAmount()), //incentive
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

    public static Double calculatePercentage(Double mainAmount){
        df.setRoundingMode(RoundingMode.DOWN);
        Double percentage;
        percentage = (incentivePercentage / 100f) * mainAmount;
        return Double.valueOf(df.format(percentage));
    }
}
