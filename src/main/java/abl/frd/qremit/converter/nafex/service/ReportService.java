package abl.frd.qremit.converter.nafex.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class ReportService {
    @Autowired
    ReportModelRepository reportModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    NafexModelRepository nafexModelRepository;
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
    @Autowired
    FileInfoModelService fileInfoModelService;
    @Autowired
    OnlineModelService onlineModelService;
    @Autowired
    BeftnModelService beftnModelService;
    @Autowired
    AccountPayeeModelService accountPayeeModelService;
    @Autowired
    CocPaidModelService cocPaidModelService;
    @Autowired
    TemporaryReportRepository temporaryReportRepository;
    @Autowired
    TemporaryReportService temporaryReportService;
    LocalDateTime currentDateTime = LocalDateTime.now();

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
        parameters.put("REPORT_DATA_SOURCE", dataSource);
        if (format.equalsIgnoreCase("pdf")) {
            // Load the JRXML file for PDF format
            file = ResourceUtils.getFile("classpath:dailyStatementDetails_pdf_tabular.jrxml");
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
    public List<ExchangeReportDTO> getAllDailyReportData(){
        List<ExchangeReportDTO> report = new ArrayList<>();
        List<ReportModel> reportModelsList = reportModelRepository.findAll(); // Have to apply logic to fetch specefic data based on date and processed flag
        Map<String, ExchangeReportDTO> reportMap = new HashMap<>();
        if(isListValid(reportModelsList)){
            for(ReportModel reportModel:reportModelsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setEnteredDate(reportModel.getDownloadDateTime());
                report.add(exchangeReportDTO);
            }
        }
        return report;
    }
    public List<ExchangeReportDTO> generateSummaryOfDailyStatement() {
        List<ExchangeReportDTO> report = getAllDailyReportData();
        report = aggregateExchangeReports(report);
        return report;
    }
    public static List<ExchangeReportDTO> aggregateExchangeReports(List<ExchangeReportDTO> exchangeReports) {
        // Group by exchangeCode
        return exchangeReports.stream()
                .collect(Collectors.groupingBy(ExchangeReportDTO::getExchangeCode))
                .entrySet().stream()
                .map(entry -> {
                    String exchangeCode = entry.getKey();
                    List<ExchangeReportDTO> reportsWithSameCode = entry.getValue();

                    // Create a new ExchangeReportDTO for the aggregated result
                    ExchangeReportDTO aggregatedReport = new ExchangeReportDTO();
                    aggregatedReport.setExchangeCode(exchangeCode);
                    aggregatedReport.setExchangeName(reportsWithSameCode.get(0).getExchangeName());  // Assuming same exchangeName

                    // Aggregate amount and count the rows
                    reportsWithSameCode.forEach(report -> {
                        aggregatedReport.doSum(report.getAmount());
                        aggregatedReport.doCount();
                    });
                    return aggregatedReport;
                })
                .collect(Collectors.toList());
    }

    public List<ExchangeReportDTO> generateSummaryOfDailyStatement_1() {
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
        List<ExchangeReportDTO> exchangeReportDTOSList = getAllDailyReportData();
        return exchangeReportDTOSList;
    }
    public List<ExchangeReportDTO> generateDetailsOfDailyStatement_1() {
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

    public Map<String, Object> processReport(String currentDate){
        Map<String, Object> resp = new HashMap<>();
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelService.loadAllIsApiExchangeHouse(1);
        List<Map<String, Object>> settlementList = fileInfoModelService.getSettlementList(exchangeHouseModelList, currentDate);
        int totalCount = 0;
        //check all settlement file uploaded
        for(Map<String, Object> settlement: settlementList){
            int count = (int) settlement.get("count");
            if(count == 1)  totalCount++;
        }
        if(totalCount !=5)  return CommonService.getResp(1, "Please upload all settlement file", null);

        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(currentDate);
        //parse data
        for(Map<String, Object> settlement: settlementList){
            FileInfoModel fileInfoModel = (FileInfoModel) settlement.get("fileInfoModel");
            int onlineCount = Integer.parseInt(fileInfoModel.getOnlineCount());
            int beftnCount = Integer.parseInt(fileInfoModel.getBeftnCount());
            int accPayeeCount = Integer.parseInt(fileInfoModel.getAccountPayeeCount());
            if(onlineCount >= 1){
                List<OnlineModel> onlineModelList = onlineModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(onlineModelList, "1");
            }
            if(accPayeeCount >= 1){
                List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(accountPayeeModelList, "2");
            }
            if(beftnCount >= 1){
                List<BeftnModel> beftnModelList = beftnModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(beftnModelList, "3");
            }
            if(("333333").equals(fileInfoModel.getExchangeCode())){
                List<CocPaidModel> cocPaidModelList = cocPaidModelService.getProcessedDataByFileId(fileInfoModel.getId(), 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(cocPaidModelList, "4");
            }

            //insert data from temporary table
            List<TemporaryReportModel> temporaryReportModelList = temporaryReportRepository.findAll();
            resp = setReportModelData(temporaryReportModelList, "");
        }
        resp = CommonService.getResp(0, "Data Processed successfully", null);
        return resp;
    }

    public <T> Map<String, Object> setReportModelData(List<T> modelList, String type){
        Map<String, Object> resp = new HashMap<>();
        String types = type;
        if(modelList != null && !modelList.isEmpty()){
            for(T model: modelList){
                ReportModel reportModel = new ReportModel();
                try{
                    String transactionNo = (String) CommonService.getPropertyValue(model, "getTransactionNo");
                    String exchangeCode = (String) CommonService.getPropertyValue(model, "getExchangeCode");
                    Double amount = (Double) CommonService.getPropertyValue(model, "getAmount");
                    Optional<ReportModel> report = reportModelRepository.findByExchangeCodeAndTransactionNoAndAmount(exchangeCode, transactionNo, amount);
                    if(report.isPresent()) continue;
                    
                    int id = (int) CommonService.getPropertyValue(model, "getId");
                    if(("").equals(type)){
                        types = (String) CommonService.getPropertyValue(model, "getType");
                        reportModel.setUploadUserId((int) CommonService.getPropertyValue(model, "getFileInfoModelId"));
                        reportModel.setFileInfoModelId((int) CommonService.getPropertyValue(model, "getFileInfoModelId"));
                        id = (int) CommonService.getPropertyValue(model, "getDataModelId");
                    }else{
                        User user = (User) CommonService.getPropertyValue(model, "getUserModel");
                        reportModel.setUploadUserId((int) user.getId());
                        FileInfoModel fileInfoModel= (FileInfoModel) CommonService.getPropertyValue(model, "getFileInfoModel");
                        reportModel.setFileInfoModelId((int) fileInfoModel.getId());
                    }

                    String branchMethod = (("3").matches(type)) ? "getRoutingNo": "getBranchCode";
                    String downloadTimeMethod = (("4").matches(type)) ? "getUploadDateTime":"getDownloadDateTime";
                    reportModel.setExchangeCode(exchangeCode);
                    reportModel.setTransactionNo(transactionNo);
                    reportModel.setBankCode((String) CommonService.getPropertyValue(model, "getBankCode"));
                    reportModel.setBankName((String) CommonService.getPropertyValue(model, "getBankName"));
                    reportModel.setBranchCode((String) CommonService.getPropertyValue(model, branchMethod));
                    reportModel.setBranchName((String) CommonService.getPropertyValue(model, "getBranchName"));
                    reportModel.setAmount(amount);
                    reportModel.setBeneficiaryName((String) CommonService.getPropertyValue(model, "getBeneficiaryName"));
                    reportModel.setBeneficiaryAccount((String) CommonService.getPropertyValue(model, "getBeneficiaryAccount"));
                    reportModel.setRemitterName((String) CommonService.getPropertyValue(model, "getRemitterName"));
                    reportModel.setDownloadDateTime((LocalDateTime) CommonService.getPropertyValue(model, downloadTimeMethod));
                    reportModel.setIncentive((Double) CommonService.getPropertyValue(model, "getIncentive"));
                    reportModel.setUploadDateTime((LocalDateTime) CommonService.getPropertyValue(model, "getUploadDateTime"));
                    reportModel.setReportDate(currentDateTime);
                    reportModel.setType(types);
                    //System.out.println(reportModel);
                    reportModelRepository.save(reportModel);
                    setIsVoucherGenerated(types, id, currentDateTime);
                }catch(Exception e){
                    e.printStackTrace();
                    return CommonService.getResp(1, "Error processing model " + e.getMessage(), null);
                }
            }
            if(("").equals(type))  temporaryReportService.truncateTemporaryReportModel();
            resp = CommonService.getResp(0, "Data processed successfully", null);
        }else{
            resp = CommonService.getResp(1, "Model can not be null or empty", null);
        }
        return resp;
    }

    public void setIsVoucherGenerated(String type, int id, LocalDateTime reportDate){
        switch (type) {
            case "1":
                onlineModelService.updateIsVoucherGenerated(id, 1, reportDate);
                break;
            case "2":
                accountPayeeModelService.updateIsVoucherGenerated(id, 1, reportDate);
                break;
            case "3":
                beftnModelService.updateIsVoucherGenerated(id, 1, reportDate);
            case "4":
                cocPaidModelService.updateIsVoucherGenerated(id, 1, reportDate);
                break;
            default:
                break;
        }
    }


}
