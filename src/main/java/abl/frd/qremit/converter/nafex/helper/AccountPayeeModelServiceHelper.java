package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
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
public class AccountPayeeModelServiceHelper {
    public static ByteArrayInputStream AccountPayeeModelToCSV(List<AccountPayeeModel> accountPayeeModelList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NON_NUMERIC);

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
                        null,
                        accountPayeeModel.getBeneficiaryAccount().trim(),
                        accountPayeeModel.getBeneficiaryName().trim(),
                        null,
                        null,
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
