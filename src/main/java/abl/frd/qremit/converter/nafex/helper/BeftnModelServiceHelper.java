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
            cell0.setCellValue(beftnModel.getExtraE());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(beftnModel.getBeneficiaryName());
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("Beftn.xls");
            workbook.write(fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }

}
