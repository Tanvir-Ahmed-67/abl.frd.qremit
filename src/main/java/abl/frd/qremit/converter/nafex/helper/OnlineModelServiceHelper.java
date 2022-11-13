package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.OnlineModel;
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
public class OnlineModelServiceHelper {
    public static ByteArrayInputStream OnlineModelToCSV(List<OnlineModel> onlineModelList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NON_NUMERIC);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (OnlineModel onlineModel : onlineModelList) {
                List<Object> data = Arrays.asList(
                        onlineModel.getTransactionNo(),
                      //  onlineModel.getCreditMark(),
                      //  onlineModel.getEnteredDate(),
                      //  onlineModel.getCurrency(),
                        onlineModel.getAmount(),
                        onlineModel.getRemitterName(),
                        onlineModel.getExchangeCode(),
                     //   onlineModel.getBankName(),
                     //   onlineModel.getBranchName(),
                        null,
                        onlineModel.getBeneficiaryAccount(),
                        onlineModel.getBeneficiaryName(),
                        null,
                        null,
                        //apiModel.getBankCode(),
                        "4006",
                        "PRINCIPAL BRANCH",
                        "PRINCIPAL CORP.BR.",
                        "22",
                        "1",
                        "0000", //incentive
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
}
