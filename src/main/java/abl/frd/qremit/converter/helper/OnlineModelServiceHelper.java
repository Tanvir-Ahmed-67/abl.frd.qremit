package abl.frd.qremit.converter.helper;
import abl.frd.qremit.converter.model.OnlineModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
@Component
public class OnlineModelServiceHelper {

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

    public static ByteArrayInputStream OnlineModelToCSV(List<OnlineModel> onlineModelList) {
        final CSVFormat format = CSVFormat.DEFAULT;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (OnlineModel onlineModel : onlineModelList) {
                List<Object> data = Arrays.asList(
                        onlineModel.getTransactionNo().trim(),
                        onlineModel.getExchangeCode().trim(),
                        onlineModel.getBeneficiaryAccount().trim(),
                        onlineModel.getBeneficiaryName().trim().replace(",",""),
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

}


