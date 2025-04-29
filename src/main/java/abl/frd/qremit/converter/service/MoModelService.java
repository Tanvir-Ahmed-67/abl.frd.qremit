package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.helper.ReimbursementModelServiceHelper;
import abl.frd.qremit.converter.model.MoModel;
import abl.frd.qremit.converter.model.ReimbursementModel;
import abl.frd.qremit.converter.repository.MoModelRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MoModelService {
    @Autowired
    MoModelRepository moModelRepository;
    @Autowired
    ReportService reportService;
    @Autowired
    CommonService commonService;

    public MoModel processAndGenerateMoData(MoModel moModel){
        String moNumber = generateMoNumber();  // Generating auto incremented Mo Number for each Mo.
        moModel.setMoNumber(moNumber);
        LocalDate reportDate = moModel.getMoDate();

        List<Object> beftnData = reportService.getAllBeftnSummaryForMo(moNumber, reportDate);

        moModel.setTotalNumberBeftn((Long) beftnData.get(0));
        moModel.setTotalAmountBeftn(new BigDecimal(String.valueOf(beftnData.get(1))).setScale(2, RoundingMode.HALF_UP));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberBeftn());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountBeftn());

        List<Object> allOtherSummaryData = reportService.getAllOtherSummaryForMo(moNumber, reportDate);

        moModel.setTotalNumberAllOtherBranch((Long) allOtherSummaryData.get(0));
        moModel.setTotalAmountAllOtherBranch(new BigDecimal(String.valueOf(allOtherSummaryData.get(1))).setScale(2, RoundingMode.HALF_UP));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberAllOtherBranch());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountAllOtherBranch());

        moModel.setTotalNumberIcash(moModel.getTotalNumberIcash());
        moModel.setTotalAmountIcash(moModel.getTotalAmountIcash());
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberIcash());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountIcash());

        List<Object> onlineData = reportService.getAllOnlineSummaryForMo(moNumber, reportDate);

        moModel.setTotalNumberOnline((Long) onlineData.get(0));
        moModel.setTotalAmountOnline(new BigDecimal(String.valueOf(onlineData.get(1))).setScale(2, RoundingMode.HALF_UP));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberOnline());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountOnline());

        List<Object> apiData = reportService.getAllApiSummaryForMo(moNumber, reportDate);

        moModel.setTotalNumberApi((Long) apiData.get(0));
        moModel.setTotalAmountApi(new BigDecimal(String.valueOf(apiData.get(1))).setScale(2, RoundingMode.HALF_UP));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberApi());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountApi());

        return moModelRepository.save(moModel);
    }
    public Map<String, Object> findCmoByDateRange(LocalDate formDate, LocalDate toDate){
        Map<String, Object> resp;
        List<MoModel> moModelList =  moModelRepository.findCmoByDateRange(formDate, toDate);
        if(moModelList == null || moModelList.isEmpty()) {
            resp = CommonService.getResp(1,"No Data Found", null);
        }
        else{
            resp = CommonService.getResp(0,"", null);
            resp.put("data", moModelList);
        }
        return resp;
    }
    public byte[] loadAllCmoByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<MoModel> moModelList = moModelRepository.findCmoByDateRange(fromDate, toDate);
        byte[] in = cmoModelsToExcelForCAD(moModelList, fromDate, toDate);
        return in;
    }
    public static byte[] cmoModelsToExcelForCAD(List<MoModel> moModelList, LocalDate fromDate, LocalDate toDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CMO_" +fromDate+"_To_"+toDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        byte[] xls = null;
        int rowIndex = 0;

        // Create center-aligned style with borders
        CellStyle centeredStyle = workbook.createCellStyle();
        centeredStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centeredStyle.setBorderTop(BorderStyle.THIN);
        centeredStyle.setBorderBottom(BorderStyle.THIN);
        centeredStyle.setBorderLeft(BorderStyle.THIN);
        centeredStyle.setBorderRight(BorderStyle.THIN);

        // Create bold style for header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.cloneStyleFrom(centeredStyle);
        headerStyle.setFont(headerFont);

        // Header titles
        String[] headers = {
                "Data Type", "Reporting Branch", "Transaction Date", "Transaction Type",
                "Second Branch", "Originating Date(For RSP Transaction)", "Advice Prefix",
                "Advice No", "TR Code", "Debit Amount", " Credit Amount", "Particular"
        };
        // Create header row
        Row headerRow = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        // Create data rows
        for (MoModel moModel : moModelList) {
            Row row = sheet.createRow(rowIndex++);
            String[] rowData = {
                    "T",
                    "00011(104)",
                    moModel.getMoDate().format(formatter),
                    "01",
                    "4006",
                    moModel.getMoDate().format(formatter),
                    "",
                    moModel.getMoNumber(),
                    "07",
                    "",
                    moModel.getGrandTotalAmount().toPlainString(),
                    ""
            };
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rowData[i]);
                cell.setCellStyle(centeredStyle);
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            workbook.write(fos);
            xls = fos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xls;
    }
    public MoModel updateMo(MoModel mo, Map<String, String> formData){
        mo.setTotalNumberIcash(Long.valueOf(formData.get("totalNumberIcash")));
        mo.setTotalAmountIcash(new BigDecimal(formData.get("totalAmountIcash")));
        mo.setGrandTotalNumber(mo.getTotalNumberBeftn() + mo.getTotalNumberIcash() + mo.getTotalNumberApi() + mo.getTotalNumberOnline() + mo.getTotalNumberAllOtherBranch());
        BigDecimal grandTotalAmount = mo.getTotalAmountBeftn().add(mo.getTotalAmountIcash())
                .add(mo.getTotalAmountApi()).add(mo.getTotalAmountOnline()).add(mo.getTotalAmountAllOtherBranch());
        mo.setGrandTotalAmount(grandTotalAmount);
        return moModelRepository.save(mo);
    }

    public MoModel findMoByDate(String date){
        LocalDate reportDate = LocalDate.parse(date);
        return moModelRepository.findByMoGenerationDate(reportDate);
    }

    public MoModel generateMoDTOForPreparingPdfFile(MoModel moModel, String date){
        MoModel model = new MoModel();
        model.setMoNumber(moModel.getMoNumber());
        model.setId(moModel.getId());
        model.setMoDate(LocalDate.parse(date));

        // For BEFTN
        model.setTotalNumberBeftn(moModel.getTotalNumberBeftn());
        model.setTotalAmountBeftn(moModel.getTotalAmountBeftn());
        model.doSumGrandTotalNumber(moModel.getTotalNumberBeftn());
        model.doSumGrandTotalAmount(moModel.getTotalAmountBeftn());
        // For All Other Brancs
        model.setTotalNumberAllOtherBranch(moModel.getTotalNumberAllOtherBranch());
        model.setTotalAmountAllOtherBranch(moModel.getTotalAmountAllOtherBranch());
        model.doSumGrandTotalNumber(model.getTotalNumberAllOtherBranch());
        model.doSumGrandTotalAmount(model.getTotalAmountAllOtherBranch());
        // For I Cash
        model.setTotalNumberIcash(moModel.getTotalNumberIcash());
        model.setTotalAmountIcash(moModel.getTotalAmountIcash());
        model.doSumGrandTotalNumber(model.getTotalNumberIcash());
        model.doSumGrandTotalAmount(model.getTotalAmountIcash());
        //For Online A/C Transfer
        model.setTotalNumberOnline(moModel.getTotalNumberOnline());
        model.setTotalAmountOnline(moModel.getTotalAmountOnline());
        model.doSumGrandTotalNumber(model.getTotalNumberOnline());
        model.doSumGrandTotalAmount(model.getTotalAmountOnline());
        //For API
        model.setTotalNumberApi(moModel.getTotalNumberApi());
        model.setTotalAmountApi(moModel.getTotalAmountApi());
        model.doSumGrandTotalNumber(model.getTotalNumberApi());
        model.doSumGrandTotalAmount(model.getTotalAmountApi());

        return model;
    }
    public byte[] generateMoInPdfFormat(MoModel moModel, String date) throws Exception {
        List<MoModel> moModelList = Collections.singletonList(moModel);
        JasperReport jasperReport = loadJasperReport("mo.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource( moModelList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        Path reportPath = commonService.getReportFile(commonService.generateFileName("mo_", date, ".pdf"));
        String outputFile = reportPath.toString();
        if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        }
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public JasperReport loadJasperReport(String fileName) throws Exception {
        Resource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            return JasperCompileManager.compileReport(inputStream);
        }
    }
    public String generateMoNumber() {
        String yearSuffix = String.format("%02d", Year.now().getValue() % 100); // e.g., 25
        String prefix = "FRD-MO-1-Txn" + yearSuffix + "-";
        Long maxSuffix = moModelRepository.findMaxMoNumberSuffix(prefix);
        long next = (maxSuffix != null) ? maxSuffix + 1 : 80;
        return prefix + next;
    }
}
