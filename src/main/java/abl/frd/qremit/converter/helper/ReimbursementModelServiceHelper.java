package abl.frd.qremit.converter.helper;

import abl.frd.qremit.converter.model.ReimbursementModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Iterator;
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
    public ReimbursementModelServiceHelper(@Value("${incentive.percentage}") float govtIncentivePercentage, @Value("${incentive.percentage}") float agraniIncentivePercentage, @Value("12665") String mainAccountNoForReimbursement, @Value("12661") String govtIncentiveAccountNoForReimbursement, @Value("12665") String agraniIncentiveAccountNoForReimbursement) {
        this.govtIncentivePercentage = govtIncentivePercentage;
        this.agraniIncentivePercentage = agraniIncentivePercentage;
        this.mainAccountNoForReimbursement = mainAccountNoForReimbursement;
        this.govtIncentiveAccountNoForReimbursement = govtIncentiveAccountNoForReimbursement;
        this.agraniIncentiveAccountNoForReimbursement = agraniIncentiveAccountNoForReimbursement;
    }
    public static byte[] ReimbursementModelsToExcel(List<ReimbursementModel> reimbursementModelList, LocalDate localDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Reimbursement_"+localDate);
        Iterator<ReimbursementModel> iterator = reimbursementModelList.iterator();
        byte[] xls = null;
        int rowIndex = 0;
        int count=1;
        Row row = null;
        // First Portion: Fill cell 4 with mainAmount if non-zero
        while (iterator.hasNext()) {
            ReimbursementModel reimbursementModel = iterator.next();
            if (reimbursementModel.getMainAmount() != 0) {
                row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(count);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(getMainAccountNoForReimbursement().trim());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(reimbursementModel.getBranchCode().trim());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(reimbursementModel.getBranchName().trim());

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(reimbursementModel.getMainAmount());
                count++;
            }
        }
        // Second Portion: Fill cell 4 with govtIncentiveAmount if non-zero
        iterator = reimbursementModelList.iterator();
        count = 1;
        while (iterator.hasNext()) {
            ReimbursementModel reimbursementModel = iterator.next();
            if (reimbursementModel.getGovtIncentiveAmount() != 0) {
                row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rowIndex);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(getGovtIncentiveAccountNoForReimbursement().trim());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(reimbursementModel.getBranchCode().trim());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(reimbursementModel.getBranchName().trim());

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(reimbursementModel.getGovtIncentiveAmount());
                count++;
            }
        }
        // Third Portion: Fill cell 4 with agraniIncentiveAmount if non-zero
        iterator = reimbursementModelList.iterator();
        count = 1;
        while (iterator.hasNext()) {
            ReimbursementModel reimbursementModel = iterator.next();

            if (reimbursementModel.getAgraniIncentiveAmount() != 0) {
                row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rowIndex);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(getAgraniIncentiveAccountNoForReimbursement().trim());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(reimbursementModel.getBranchCode().trim());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(reimbursementModel.getBranchName().trim());

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(reimbursementModel.getAgraniIncentiveAmount());
                count++;
            }
        }
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ByteArrayInputStream is = null;
        try {
            workbook.write(fos);
            xls = fos.toByteArray();
            is = new ByteArrayInputStream(xls);
            fos.close();
            is.close();
            workbook.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return xls;
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
