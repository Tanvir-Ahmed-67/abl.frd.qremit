package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.OnlineModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OnlineModelServiceHelper {


    private static float incentivePercentage;
    @Autowired
    public OnlineModelServiceHelper(@Value("${incentive.percentage}") float incentivePercentage) {
        this.incentivePercentage = incentivePercentage;
    }

    public static ByteArrayInputStream OnlineModelToCSV(List<OnlineModel> onlineModelList) {
        final CSVFormat format = CSVFormat.DEFAULT;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (OnlineModel onlineModel : onlineModelList) {
                onlineModel.setBeneficiaryAccount(getOnlineAccountNumber(onlineModel.getBeneficiaryAccount()));
                List<Object> data = Arrays.asList(
                        onlineModel.getTransactionNo().trim(),
                        onlineModel.getExchangeCode().trim(),
                        onlineModel.getBeneficiaryAccount().trim(),
                        onlineModel.getBeneficiaryName().trim(),
                        onlineModel.getAmount()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to Download " + e.getMessage());
        }
    }

    public static String getOnlineAccountNumber(String accountNumber){
        //^.*(02000\d{8})$.*
        Pattern p = Pattern.compile("^.*(02000\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        String updatedOlineAccountNumber=null;
        if (m.find())
        {
            updatedOlineAccountNumber = m.group(1);
        }
        return updatedOlineAccountNumber;
    }
}


