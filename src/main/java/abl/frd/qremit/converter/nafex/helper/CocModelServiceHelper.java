package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.CocModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Component
public class CocModelServiceHelper {

    public static ByteArrayInputStream cocModelToCSV(List<CocModel> cocDataModelList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NON_NUMERIC);

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
                        null,
                        cocDataModel.getBeneficiaryAccount().trim(),
                        cocDataModel.getBeneficiaryName().trim(),
                        null,
                        null,
                        "4006",
                        "PRINCIPAL BRANCH",
                        "PRINCIPAL CORP.BR.",
                        "22",
                        "1",
                        "0000", //incentive
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
}
