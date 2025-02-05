package abl.frd.qremit.converter.helper;

import abl.frd.qremit.converter.model.CocModel;
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
public class CocModelServiceHelper {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static float incentivePercentage;
    @Autowired
    public CocModelServiceHelper(@Value("${incentive.percentage}") float incentivePercentage) {
        this.incentivePercentage = incentivePercentage;
    }

    public static ByteArrayInputStream cocModelToCSV(List<CocModel> cocDataModelList) {
        final CSVFormat format = CSVFormat.DEFAULT;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (CocModel cocDataModel : cocDataModelList) {
                List<Object> data = Arrays.asList(
                        cocDataModel.getTransactionNo().trim(),
                        cocDataModel.getCreditMark().trim(),
                        cocDataModel.getEnteredDate().trim(),
                        cocDataModel.getCurrency().trim(),
                        cocDataModel.getAmount(),
                        cocDataModel.getRemitterName().trim(),
                        cocDataModel.getExchangeCode().trim(),
                        cocDataModel.getBankName().trim(),
                        cocDataModel.getBranchName().trim(),
                        "NULL",
                        cocDataModel.getBeneficiaryAccount().trim(),
                        cocDataModel.getBeneficiaryName().trim(),
                        "NULL",
                        "NULL",
                        "4006",
                        "PRINCIPAL BRANCH",
                        "PRINCIPAL CORP.BR.",
                        "22",
                        "1",
                        calculatePercentage(cocDataModel.getAmount()),    //incentive
                        "5"
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
