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
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
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
        JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Report"+"\\Report.pdf");
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


/*
    public String generateJasperReport(String format) throws JRException, FileNotFoundException {
        // Retrieve data from the database
        //List<NafexEhMstModel> dataList = nafexModelRepository.findAll();
        List<ExchangeReportDTO> dataList = generateSummaryOfDailyStatement();
        for(ExchangeReportDTO exchangeReportDTO: dataList){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
        }
        HtmlExporter exporter = new HtmlExporter();
        StringWriter stringWriter = new StringWriter();

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

        // Export the report to the desired format
        if (format.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdf(jasperPrint);
            JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Report"+"\\Report.pdf");
        }
        if (format.equalsIgnoreCase("html")) {
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleHtmlExporterOutput(stringWriter));
            exporter.exportReport();
        }
        // Add support for other formats (e.g., Excel) as needed
        return stringWriter.toString();
    }
 */
    public List<ExchangeReportDTO> generateSummaryOfDailyStatement() {
        List<ExchangeReportDTO> report = new ArrayList<>();

        // Fetch data from each table
        List<OnlineModel> onlineData = onlineModelRepository.findAll();
        List<BeftnModel> beftnData = beftnModelRepository.findAll();
        List<CocModel> cocData = cocModelRepository.findAll();
        List<AccountPayeeModel> accountPayeeData = accountPayeeModelRepository.findAll();

        // Combine the Online data by exchangeCode
        Map<String, ExchangeReportDTO> reportMap = new HashMap<>();
        for (OnlineModel item : onlineData) {
            String exchangeCode = item.getExchangeCode();
            ExchangeReportDTO dto = reportMap.computeIfAbsent(exchangeCode, k -> new ExchangeReportDTO(k, item.getTransactionNo(), item.getAmount(), item.getBeneficiaryName(), item.getBeneficiaryAccount(),item.getFileInfoModel().getUploadDateTime()));
            dto.doSum(item.getAmount());
            dto.doCount();
        }

        // Process data from beftn table
        for (BeftnModel item : beftnData) {
            String exchangeCode = item.getExchangeCode();
            reportMap.computeIfPresent(exchangeCode, (k, v) -> {
                v.setTransactionNo(item.getTransactionNo());
                v.setAmount(item.getAmount());
                v.setBeneficiaryName(item.getBeneficiaryName());
                v.setBeneficiaryAccount(item.getBeneficiaryAccount());
                v.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                v.doSum(item.getAmount());
                v.doCount();
                return v;
            });
        }

        // Process data from coc table
        for (CocModel item : cocData) {
            String exchangeCode = item.getExchangeCode();
            reportMap.computeIfPresent(exchangeCode, (k, v) -> {
                v.setTransactionNo(item.getTransactionNo());
                v.setAmount(item.getAmount());
                v.setBeneficiaryName(item.getBeneficiaryName());
                v.setBeneficiaryAccount(item.getBeneficiaryAccount());
                v.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                v.doSum(item.getAmount());
                v.doCount();
                return v;
            });
        }

        // Process data from account payee table
        for (AccountPayeeModel item : accountPayeeData) {
            String exchangeCode = item.getExchangeCode();
            reportMap.computeIfPresent(exchangeCode, (k, v) -> {
                v.setTransactionNo(item.getTransactionNo());
                v.setAmount(item.getAmount());
                v.setBeneficiaryName(item.getBeneficiaryName());
                v.setBeneficiaryAccount(item.getBeneficiaryAccount());
                v.setEnteredDate(item.getFileInfoModel().getUploadDateTime());
                v.doSum(item.getAmount());
                v.doCount();
                return v;
            });
        }
        return new ArrayList<>(reportMap.values());
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
