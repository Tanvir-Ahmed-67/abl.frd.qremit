package abl.frd.qremit.converter.nafex.service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abl.frd.qremit.converter.nafex.repository.ErrorDataModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.ReportRepository;
import org.springframework.util.ResourceUtils;

@Service
public class ReportService {
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    NafexModelRepository nafexModelRepository;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    ErrorDataModelRepository errorDataModelRepository;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;

    public Map<String, Object> getFileDetails(String tableName, String fileInfoId) {
        return reportRepository.getFileDetails(tableName, fileInfoId);
    }
    

    public List<ErrorDataModel> findByUserModelId(int userId) {
        return errorDataModelRepository.findByUserModelId(userId);
    }

    public byte[] generateDailyStatementInPdfFormat(List<ExchangeReportDTO> dataList) throws JRException, FileNotFoundException {
        for(ExchangeReportDTO exchangeReportDTO: dataList){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
        }
        // Load File And Compile It.
        File file = ResourceUtils.getFile("classpath:dailyStatementSummary.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        // Convert data into a JasperReports data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        // Parameters map if needed
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Report"+"\\Summary_Report.pdf");
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] generateDetailsJasperReport(List<ExchangeReportDTO> dataList, String format) throws JRException, FileNotFoundException {
        File file;
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for(ExchangeReportDTO exchangeReportDTO: dataList){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
        }
        // Convert data into a JasperReports data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        // Parameters map if needed
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        if (format.equalsIgnoreCase("pdf")) {
            // Load the JRXML file for PDF format
            file = ResourceUtils.getFile("classpath:dailyStatementDetails_pdf.jrxml");
            jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Report\\Details_Report.pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        } else if (format.equalsIgnoreCase("csv")) {
            // Load the JRXML file for CSV format
            file = ResourceUtils.getFile("classpath:dailyStatementDetails_csv.jrxml");
            jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // CSV Exporter Setup for File Generation
            JRCsvExporter fileExporter = new JRCsvExporter();
            fileExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            fileExporter.setExporterOutput(new SimpleWriterExporterOutput("D:\\Report\\Details_Report.csv"));

            // Optional: Set CSV configuration (e.g., delimiter)
            SimpleCsvExporterConfiguration fileConfiguration = new SimpleCsvExporterConfiguration();
            fileConfiguration.setFieldDelimiter(",");
            fileExporter.setConfiguration(fileConfiguration);
            fileExporter.exportReport();

            // CSV Exporter Setup for Download
            JRCsvExporter downloadExporter = new JRCsvExporter();
            downloadExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            downloadExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));

            // Reuse the same configuration for the download
            downloadExporter.setConfiguration(fileConfiguration);
            downloadExporter.exportReport();
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return outputStream.toByteArray();
    }

    public List<ExchangeReportDTO> generateSummaryOfDailyStatement() {
        List<ExchangeReportDTO> report = new ArrayList<>();

        // Fetch data from each table
        List<OnlineModel> onlineData = onlineModelRepository.findAll();
        List<BeftnModel> beftnData = beftnModelRepository.findAll();
        List<CocModel> cocData = cocModelRepository.findAll();
        List<AccountPayeeModel> accountPayeeData = accountPayeeModelRepository.findAll();

        Map<String, ExchangeReportDTO> reportMap = new HashMap<>();

        // Process onlineData if it has valid data
        if (isListValid(onlineData)) {
            for (OnlineModel item : onlineData) {
                String exchangeCode = item.getExchangeCode();
                ExchangeReportDTO dto = reportMap.computeIfAbsent(exchangeCode, k -> new ExchangeReportDTO(
                        k, item.getTransactionNo(), item.getAmount(), item.getBeneficiaryName(),
                        item.getBeneficiaryAccount(), item.getFileInfoModel().getUploadDateTime()));
                dto.doSum(item.getAmount());
                dto.doCount();
            }
        }
        // Process beftnData if it has valid data
        if (isListValid(beftnData)) {
            for (BeftnModel item : beftnData) {
                String exchangeCode = item.getExchangeCode();
                reportMap.computeIfAbsent(exchangeCode, k -> new ExchangeReportDTO(
                        k, item.getTransactionNo(), item.getAmount(), item.getBeneficiaryName(),
                        item.getBeneficiaryAccount(), item.getFileInfoModel().getUploadDateTime()));
                ExchangeReportDTO dto = reportMap.get(exchangeCode); // Get the existing or newly created DTO
                dto.setTransactionNo(item.getTransactionNo());
                dto.setAmount(dto.getAmount() + item.getAmount()); // Aggregate the amount
                dto.setBeneficiaryName(item.getBeneficiaryName());
                dto.setBeneficiaryAccount(item.getBeneficiaryAccount());
                dto.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                dto.doSum(item.getAmount());
                dto.doCount();
            }
        }
        // Process cocData if it has valid data
        if (isListValid(cocData)) {
            for (CocModel item : cocData) {
                String exchangeCode = item.getExchangeCode();
                reportMap.computeIfAbsent(exchangeCode, k -> new ExchangeReportDTO(
                        k, item.getTransactionNo(), item.getAmount(), item.getBeneficiaryName(),
                        item.getBeneficiaryAccount(), item.getFileInfoModel().getUploadDateTime()));
                ExchangeReportDTO dto = reportMap.get(exchangeCode); // Get the existing or newly created DTO
                dto.setTransactionNo(item.getTransactionNo());
                dto.setAmount(dto.getAmount() + item.getAmount()); // Aggregate the amount
                dto.setBeneficiaryName(item.getBeneficiaryName());
                dto.setBeneficiaryAccount(item.getBeneficiaryAccount());
                dto.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                dto.doSum(item.getAmount());
                dto.doCount();
            }
        }
        // Process accountPayeeData if it has valid data
        if (isListValid(accountPayeeData)) {
            for (AccountPayeeModel item : accountPayeeData) {
                String exchangeCode = item.getExchangeCode();
                reportMap.computeIfAbsent(exchangeCode, k -> new ExchangeReportDTO(
                        k, item.getTransactionNo(), item.getAmount(), item.getBeneficiaryName(),
                        item.getBeneficiaryAccount(), item.getFileInfoModel().getUploadDateTime()));
                ExchangeReportDTO dto = reportMap.get(exchangeCode); // Get the existing or newly created DTO
                dto.setTransactionNo(item.getTransactionNo());
                dto.setAmount(dto.getAmount() + item.getAmount()); // Aggregate the amount
                dto.setBeneficiaryName(item.getBeneficiaryName());
                dto.setBeneficiaryAccount(item.getBeneficiaryAccount());
                dto.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                dto.doSum(item.getAmount());
                dto.doCount();
            }
        }
        // Convert the map values to a list for the final report
        if (reportMap.isEmpty()) {
            report.add(new ExchangeReportDTO("No data available", null, 0.0, "", "", null));
        } else {
            report.addAll(reportMap.values());
        }
        return report;
    }
    // Utility method to check if a list is valid (not null, not empty, and size > 0)
    private boolean isListValid(List<?> list) {
        return list != null && !list.isEmpty() && list.size() > 0;
    }
    public List<ExchangeReportDTO> generateDetailsOfDailyStatement() {
        // Fetch data from each table
        List<OnlineModel> onlineData = onlineModelRepository.findAll();
        List<BeftnModel> beftnData = beftnModelRepository.findAll();
        List<CocModel> cocData = cocModelRepository.findAll();
        List<AccountPayeeModel> accountPayeeData = accountPayeeModelRepository.findAll();

        // Merge all four data list into a single list
        List<ExchangeReportDTO> allData = mergeData(onlineData, beftnData, cocData, accountPayeeData);
        return allData;
    }

    private ExchangeReportDTO convertOnlineModelToDTO(OnlineModel onlineModel) {
        ExchangeReportDTO dto = new ExchangeReportDTO();
        // Populate DTO fields from OnlineModel
        dto.setTransactionNo(onlineModel.getTransactionNo());
        dto.setExchangeCode(onlineModel.getExchangeCode());
        dto.setAmount(onlineModel.getAmount());
        dto.setEnteredDate(onlineModel.getFileInfoModel().getUploadDateTime());
        dto.setBeneficiaryName(onlineModel.getBeneficiaryName());
        dto.setBeneficiaryAccount(onlineModel.getBeneficiaryAccount());
        dto.setRemitterName(onlineModel.getRemitterName());
        // Set other fields as needed
        return dto;
    }
    private ExchangeReportDTO convertBeftnModelToDTO(BeftnModel beftnModel ) {
        ExchangeReportDTO dto = new ExchangeReportDTO();
        // Populate DTO fields from BeftnModel
        dto.setTransactionNo(beftnModel.getTransactionNo());
        dto.setExchangeCode(beftnModel.getExchangeCode());
        dto.setAmount(beftnModel.getAmount());
        dto.setEnteredDate(beftnModel.getFileInfoModel().getUploadDateTime());
        dto.setBeneficiaryName(beftnModel.getBeneficiaryName());
        dto.setBeneficiaryAccount(beftnModel.getBeneficiaryAccount());
        // Set other fields as needed
        return dto;
    }
    private ExchangeReportDTO convertCocModelToDTO(CocModel cocModel) {
        ExchangeReportDTO dto = new ExchangeReportDTO();
        // Populate DTO fields from CocModel
        dto.setTransactionNo(cocModel.getTransactionNo());
        dto.setExchangeCode(cocModel.getExchangeCode());
        dto.setAmount(cocModel.getAmount());
        dto.setEnteredDate(cocModel.getFileInfoModel().getUploadDateTime());
        dto.setBeneficiaryName(cocModel.getBeneficiaryName());
        dto.setBeneficiaryAccount(cocModel.getBeneficiaryAccount());
        dto.setRemitterName(cocModel.getRemitterName());
        // Set other fields as needed
        return dto;
    }
    private ExchangeReportDTO convertAccountPayeeModelToDTO(AccountPayeeModel  accountPayeeModel) {
        ExchangeReportDTO dto = new ExchangeReportDTO();
        // Populate DTO fields from AccountPayeeModel
        dto.setTransactionNo(accountPayeeModel.getTransactionNo());
        dto.setExchangeCode(accountPayeeModel.getExchangeCode());
        dto.setAmount(accountPayeeModel.getAmount());
        dto.setEnteredDate(accountPayeeModel.getFileInfoModel().getUploadDateTime());
        dto.setBeneficiaryName(accountPayeeModel.getBeneficiaryName());
        dto.setBeneficiaryAccount(accountPayeeModel.getBeneficiaryAccount());
        dto.setRemitterName(accountPayeeModel.getRemitterName());
        // Set other fields as needed
        return dto;
    }
    public List<ExchangeReportDTO> mergeData(
            List<OnlineModel> onlineData,
            List<BeftnModel> beftnData,
            List<CocModel> cocData,
            List<AccountPayeeModel> accountPayeeData) {

        List<ExchangeReportDTO> mergedList = new ArrayList<>();

        // Convert and add data from each list to the merged list
        for (OnlineModel online : onlineData) {
            mergedList.add(convertOnlineModelToDTO(online));
        }
        for (BeftnModel beftn : beftnData) {
            mergedList.add(convertBeftnModelToDTO(beftn));
        }
        for (CocModel coc : cocData) {
            mergedList.add(convertCocModelToDTO(coc));
        }
        for (AccountPayeeModel accountPayee : accountPayeeData) {
            mergedList.add(convertAccountPayeeModelToDTO(accountPayee));
        }
        return mergedList;
    }

}
