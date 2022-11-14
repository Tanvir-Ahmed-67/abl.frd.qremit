package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.BeftnModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Iterator;
import java.util.List;
@Component
public class BeftnModelServiceHelper {
    public static ByteArrayInputStream BeftnModelsToExcel(List<BeftnModel> beftnModelList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Beftn");
        Iterator<BeftnModel> iterator = beftnModelList.iterator();

        int rowIndex = 0;
        while(iterator.hasNext()){
            BeftnModel beftnModel = iterator.next();

            Row row = sheet.createRow(rowIndex++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(beftnModel.getId());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(beftnModel.getOrgCustomerNo().trim());

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(beftnModel.getOrgName().trim());

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(beftnModel.getOrgAccountNo().trim());

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(beftnModel.getOrgAccountType().trim());

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(beftnModel.getBeneficiaryName().trim());

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(beftnModel.getBeneficiaryAccount().trim());

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(beftnModel.getBeneficiaryAccountType().trim());

            Cell cell8 = row.createCell(8);
            cell8.setCellValue(beftnModel.getRoutingNo().trim());

            Cell cell9 = row.createCell(9);
            cell9.setCellValue(beftnModel.getAmount());

            Cell cell10 = row.createCell(10);
            cell10.setCellValue(beftnModel.getTransactionNo());
        }

        //FileOutputStream fos = null;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ByteArrayInputStream is = null;
        try {
            workbook.write(fos);
            byte[] xls = fos.toByteArray();
            is = new ByteArrayInputStream(xls);
            fos.close();
            is.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    return is;
    }

}
