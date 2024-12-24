package abl.frd.qremit.converter.service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import abl.frd.qremit.converter.model.*;
import abl.frd.qremit.converter.repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

@SuppressWarnings("unchecked")
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
    CocModelService cocModelService;
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
    @Autowired
    CommonService commonService;
    @Autowired
    LogModelService logModelService;
    
    public List<ErrorDataModel> findByUserModelId(int userId) {
        return errorDataModelRepository.findByUserModelId(userId);
    }

    public JasperReport loadJasperReport(String fileName) throws Exception {
        Resource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            return JasperCompileManager.compileReport(inputStream);
        }
    }

    public byte[] generateDailyStatementInPdfFormat(List<ExchangeReportDTO> dataList, String date) throws Exception {
        for(ExchangeReportDTO exchangeReportDTO: dataList){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
        }
        JasperReport jasperReport = loadJasperReport("dailyStatementSummary.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        //Path reportPath = CommonService.getReportFile("summary_report_" + date.replace("-", "_") +".pdf");
        Path reportPath = commonService.getReportFile(commonService.generateFileName("summary_report_", date, ".pdf"));
        String outputFile = reportPath.toString();
        if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        }
        
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public byte[] generateDailyVoucherInPdfFormat(List<ExchangeReportDTO> dataList, String date) throws Exception {
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        // Collect all unique exchange codes from dataList
        Set<String> exchangeCodes = dataList.stream()
                .map(ExchangeReportDTO::getExchangeCode)
                .collect(Collectors.toSet());
        // Fetch all ExchangeHouseModels for these exchange codes in one go
        Map<String, ExchangeHouseModel> exchangeHouseMap = exchangeHouseModelService.findAllByExchangeCodeIn(exchangeCodes).stream().collect(Collectors.toMap(ExchangeHouseModel::getExchangeCode, Function.identity()));
        for(int i =0; i<dataList.size();i++){
            dataList.get(i).setExchangeName(exchangeHouseMap.get(dataList.get(i).getExchangeCode()).getExchangeName());
            dataList.get(i).setNrtAccountNo(exchangeHouseMap.get(dataList.get(i).getExchangeCode()).getNrtaCode());
            dataList.get(i).setEnteredDate(currentDateTime);
        }
        // Load File And Compile It.
        JasperReport jasperReport = loadJasperReport("dailyVoucher.jrxml");
        // Convert data into a JasperReports data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        // Parameters map if needed
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        Path reportPath = commonService.getReportFile(commonService.generateFileName("daily_voucher_", date, ".pdf"));
        String outputFile = reportPath.toString();
        if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        }
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] generateDetailsJasperReport(List<ExchangeReportDTO> dataList, String format, String date) throws Exception {
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
        Path reportPath = commonService.getReportFile(commonService.generateFileName("details_report_", date, "." + format.toLowerCase()));
        String outputFile = reportPath.toString();
        if (format.equalsIgnoreCase("pdf")) {
            // Load the JRXML file for PDF format
            //file = ResourceUtils.getFile("classpath:dailyStatementDetails_pdf_tabular.jrxml");
            //jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            jasperReport = loadJasperReport("dailyStatementDetails_pdf_tabular.jrxml");
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            // Export to PDF
            if(!Files.exists(reportPath)){
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
            }
            //JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if (format.equalsIgnoreCase("csv")) {
            // Load the JRXML file for CSV format
            //file = ResourceUtils.getFile("classpath:dailyStatementDetails_csv.jrxml");
            //jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            jasperReport = loadJasperReport("dailyStatementDetails_csv.jrxml");
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // CSV Exporter Setup for File Generation
            JRCsvExporter fileExporter = new JRCsvExporter();
            fileExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            fileExporter.setExporterOutput(new SimpleWriterExporterOutput(outputFile));

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
    public List<ExchangeReportDTO> getAllDailyReportData(String date){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate reportDate = LocalDate.parse(date);
        //List<ReportModel> reportModelsList = reportModelRepository.findAll(); // Have to apply logic to fetch specefic data based on date and processed flag
        List<ReportModel> reportModelsList = reportModelRepository.getReportModelByReportDate(reportDate);
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
    public List<ExchangeReportDTO> generateSummaryOfDailyStatement(String date) {
        List<ExchangeReportDTO> report = getAllDailyReportData(date);
        report = aggregateExchangeReports(report, date);
        return report;
    }
    public List<ExchangeReportDTO> aggregateExchangeReports(List<ExchangeReportDTO> exchangeReports, String date) {
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
                        aggregatedReport.setVoucherDate(LocalDate.parse(date));
                        aggregatedReport.setNrtAccountNo(exchangeHouseModelRepository.findByExchangeCode(aggregatedReport.getExchangeCode()).getNrtaCode());
                        aggregatedReport.doSum(report.getAmount());
                        aggregatedReport.doCount();
                    });
                    return aggregatedReport;
                })
                .collect(Collectors.toList());
    }

    // Utility method to check if a list is valid (not null, not empty, and size > 0)
    private boolean isListValid(List<?> list) {
        return list != null && !list.isEmpty() && list.size() > 0;
    }
    public List<ExchangeReportDTO> generateDetailsOfDailyStatement(String date) {
        List<ExchangeReportDTO> exchangeReportDTOSList = getAllDailyReportData(date);
        return exchangeReportDTOSList;
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
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelService.loadAllIsSettlementExchangeHouse(1);
        List<Map<String, Object>> settlementList = fileInfoModelService.getSettlementList(exchangeHouseModelList, currentDate);
        int totalCount = 0;
        //check all settlement file uploaded
        int hasSettlementDailyCount = exchangeHouseModelService.calculateSumOfHasSettlementDaily(exchangeHouseModelList);
        for(Map<String, Object> settlement: settlementList){
            int count = (int) settlement.get("count");
            if(count >= 1)  totalCount++;
        }
        if(totalCount < hasSettlementDailyCount)  return CommonService.getResp(1, "Please upload all settlement file", null);
        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(currentDate);
        //parse data
        int count = 0;
        for(Map<String, Object> settlement: settlementList){
            FileInfoModel fileInfoModel = (FileInfoModel) settlement.get("fileInfoModel");
            if(fileInfoModel == null)   continue;   //for empty fileinfo no data will be generated
            int onlineCount = CommonService.convertStringToInt(fileInfoModel.getOnlineCount());
            int beftnCount = CommonService.convertStringToInt(fileInfoModel.getBeftnCount());
            int accPayeeCount = CommonService.convertStringToInt(fileInfoModel.getAccountPayeeCount());
            if(onlineCount >= 1){
                List<OnlineModel> onlineModelList = onlineModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(onlineModelList, "1");
                count += resp.size();
                if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
            }
            if(accPayeeCount >= 1){
                List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(accountPayeeModelList, "2");
                count += resp.size();
                if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
            }
            if(beftnCount >= 1){
                List<BeftnModel> beftnModelList = beftnModelService.getProcessedDataByFileId(fileInfoModel.getId(),1, 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(beftnModelList, "3");
                count += resp.size();
                if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
            }
            if(("333333").equals(fileInfoModel.getExchangeCode())){
                List<CocPaidModel> cocPaidModelList = cocPaidModelService.getProcessedDataByFileId(fileInfoModel.getId(), 0, (LocalDateTime) dateTime.get("startDateTime"),(LocalDateTime) dateTime.get("endDateTime"));
                resp = setReportModelData(cocPaidModelList, "4");
                count += resp.size();
                if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
            }

            //insert data from temporary table
            List<TemporaryReportModel> temporaryReportModelList = temporaryReportRepository.findAll();
            resp = setReportModelData(temporaryReportModelList, "");
            count += resp.size();
            if(resp.get("err") != null && (int) resp.get("err") == 1) return resp;
        }
        if(count == 0)  return CommonService.getResp(0, "No data found for processing report", null);
        resp = CommonService.getResp(0, "Data Processed successfully", null);
        return resp;
    }

    public <T> Map<String, Object> setReportModelData(List<T> modelList, String type){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        LocalDate currentDate = LocalDate.now();
        String types = type;
        List<ReportModel> reportInsertList = new ArrayList<>();
        List<Integer> onlineInsertList = new ArrayList<>();
        List<Integer> acPayeeInsertList = new ArrayList<>();
        List<Integer> beftnInsertList = new ArrayList<>();
        List<Integer> cocPaidInsertList = new ArrayList<>();
        Map<String, List<Integer>> insertList = new HashMap<>();
        if(modelList != null && !modelList.isEmpty()){
            int count = 0;
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
                        reportModel.setUploadUserId((int) CommonService.getPropertyValue(model, "getUploadUserId"));
                        reportModel.setFileInfoModelId((int) CommonService.getPropertyValue(model, "getFileInfoModelId"));
                        id = (int) CommonService.getPropertyValue(model, "getDataModelId");
                    }else{
                        User user = (User) CommonService.getPropertyValue(model, "getUserModel");
                        reportModel.setUploadUserId((int) user.getId());
                        FileInfoModel fileInfoModel= (FileInfoModel) CommonService.getPropertyValue(model, "getFileInfoModel");
                        reportModel.setFileInfoModelId((int) fileInfoModel.getId());
                    }

                    String branchMethod = (("3").equals(type)) ? "getRoutingNo": "getBranchCode";
                    String downloadTimeMethod = (("4").equals(type)) ? "getUploadDateTime":"getDownloadDateTime";
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
                    reportModel.setReportDate(currentDate);
                    reportModel.setType(types);
                    reportModel.setDataModelId(id);
                    if(("1").equals(types)) reportModel.setIsApi((Integer) CommonService.getPropertyValue(model, "getIsApi"));
                    switch (types){
                        case "1":
                            onlineInsertList.add(id);
                            insertList.put(types, onlineInsertList);
                            break;
                        case "2":
                            acPayeeInsertList.add(id);
                            insertList.put(types, acPayeeInsertList);
                            break;
                        case "3":
                            beftnInsertList.add(id);
                            insertList.put(types, beftnInsertList);
                            break;
                        case "4":
                            cocPaidInsertList.add(id);
                            insertList.put(types, cocPaidInsertList);
                            break;
                    }
                    reportInsertList.add(reportModel);
                    count++;
                }catch(Exception e){
                    e.printStackTrace();
                    return CommonService.getResp(1, "Error processing model " + e.getMessage(), null);
                }
            }
            if(count == 0)  return CommonService.getResp(0, "No data found for processing report", null);
            if(!reportInsertList.isEmpty()){
                List<ReportModel> savedModels = reportModelRepository.saveAll(reportInsertList);
                reportModelRepository.flush();
                if(!savedModels.isEmpty()){
                    for (Map.Entry<String, List<Integer>> entry : insertList.entrySet()) {
                        setIsVoucherGeneratedBulk(entry.getKey(), entry.getValue(), currentDateTime);
                    }
                    if(("").equals(type))  temporaryReportService.truncateTemporaryReportModel();
                }
                resp = CommonService.getResp(0, "Data processed successfully", null);
            }
        }
        return resp;
    }

    public void setIsVoucherGeneratedBulk(String type, List<Integer> ids, LocalDateTime reportDate){
        switch (type) {
            case "1":
                onlineModelService.updateIsVoucherGeneratedBulk(ids, 1, reportDate);
                break;
            case "2":
                accountPayeeModelService.updateIsVoucherGeneratedBulk(ids, 1, reportDate);
                break;
            case "3":
                beftnModelService.updateIsVoucherGeneratedBulk(ids, 1, reportDate);
            case "4":
                cocPaidModelService.updateIsVoucherGeneratedBulk(ids, 1, reportDate);
                break;
            default:
                break;
        }
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

    public Map<String, Object> getFileDetails(int id, Map<String,Object> fileInfo, String[] columnData ){
        Map<String, Object> resp = new HashMap<>();

        List<Map<String, Object>> fileData = (List<Map<String, Object>>) fileInfo.get("data");
        FileInfoModel fileInfoModel = fileInfoModelService.findFileInfoModelById(id);
        int onlineCount = CommonService.convertStringToInt(fileInfoModel.getOnlineCount());
        int accountPayeeCount = CommonService.convertStringToInt(fileInfoModel.getAccountPayeeCount());
        int beftnCount = CommonService.convertStringToInt(fileInfoModel.getBeftnCount());
        int cocCount = CommonService.convertStringToInt(fileInfoModel.getCocCount());
        List<OnlineModel> onlineModelList = new ArrayList<>();
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        List<BeftnModel> beftnModelList = new ArrayList<>();
        List<CocModel> cocModelList = new ArrayList<>();
        List<CocPaidModel> cocPaidModelList = new ArrayList<>();
        if(onlineCount >= 1)    onlineModelList = onlineModelService.findAllOnlineModelByFileInfoId(id);
        if(accountPayeeCount >=1)   accountPayeeModelList = accountPayeeModelService.findAllAccountPayeeModelByFileInfoId(id);
        if(beftnCount >= 1) beftnModelList = beftnModelService.findAllBeftnModelByFileInfoId(id);
        
        if(cocCount >=  1){
            if(fileInfoModel.getExchangeCode().equals("333333")){
                cocPaidModelList = cocPaidModelService.findAllCocPaidModelHavingFileInfoId(id);
            }else cocModelList = cocModelService.findAllCocModelByFileInfoId(id);
        }  
        List<Map<String, Object>> dataList = new ArrayList<>();
        int sl = 1;
        double totalAmount = 0;
        LocalDateTime downloadDateTime = null;

        for(Map<String,Object> fdata: fileData){
            String type = fdata.get("type_flag").toString();
            String transactionNo = fdata.get("transaction_no").toString();
            if(("1").equals(type)){
                for(OnlineModel onlineModel: onlineModelList){
                    if(transactionNo.equalsIgnoreCase(onlineModel.getTransactionNo())){
                        downloadDateTime = onlineModel.getDownloadDateTime();
                        break;
                    }
                }
            }
            if(("2").equals(type)){
                for(AccountPayeeModel accountPayeeModel: accountPayeeModelList){
                    if(transactionNo.equalsIgnoreCase(accountPayeeModel.getTransactionNo())){
                        downloadDateTime = accountPayeeModel.getDownloadDateTime();
                        break;
                    }
                }
            }
            if(("3").equals(type)){
                for(BeftnModel beftnModel: beftnModelList){
                    if(transactionNo.equalsIgnoreCase(beftnModel.getTransactionNo())){
                        downloadDateTime = beftnModel.getDownloadDateTime();
                        break;
                    }
                }
            }
            if(("4").equals(type)){
                //for cocPaidModel download date time will be uploaded date time
                if(!cocPaidModelList.isEmpty()){
                    for(CocPaidModel cocPaidModel: cocPaidModelList){
                        if(transactionNo.equalsIgnoreCase(cocPaidModel.getTransactionNo())){
                            downloadDateTime = cocPaidModel.getUploadDateTime();
                            break;
                        }
                    }
                }
                if(!cocModelList.isEmpty()){
                    for(CocModel cocModel: cocModelList){
                        if(transactionNo.equalsIgnoreCase(cocModel.getTransactionNo())){
                            downloadDateTime = cocModel.getDownloadDateTime();
                            break;
                        }
                    }
                }
            }

            String processedDate = CommonService.convertDateToString(downloadDateTime);
            if(processedDate.isEmpty()){
                processedDate = CommonService.generateClassForText("Not Processed", "text-danger fw-bold");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("sl", sl++);
            dataMap.put("bankName",fdata.get("bank_name"));
            dataMap.put("branchCode",fdata.get("branch_code"));
            dataMap.put("branchName",fdata.get("branch_name"));
            dataMap.put("beneficiaryAccountNo",fdata.get("beneficiary_account_no"));
            dataMap.put("beneficiaryName",fdata.get("beneficiary_name"));
            dataMap.put("remitterName",fdata.get("remitter_name"));
            dataMap.put("transactionNo",transactionNo);
            dataMap.put("exchangeCode",fdata.get("exchange_code"));
            dataMap.put("amount",fdata.get("amount"));
            dataMap.put("processedDate",processedDate);
            totalAmount += CommonService.convertStringToDouble(fdata.get("amount").toString());
            Map<String, Object> types = CommonService.getRemittanceTypes();
            dataMap.put("remType", types.get(type));
            dataList.add(dataMap);
        }
        Map<String, Object> totalAmountMap = commonService.getTotalAmountData(columnData, totalAmount,"beneficiaryAccountNo");
        dataList.add(totalAmountMap);

        resp.put("err", fileInfo.get("err"));
        resp.put("msg", fileInfo.get("msg"));
        resp.put("data",dataList);
        return resp;
    }
    
    //last row for showing total data for uploaded file info
    public Map<String, Object> calculateTotalUploadFileInfo(int totalCocCount, int totalBeftnCount, int totalOnlineCount, int totalAccountPayeeCount, int totalErrorCount, int totalCount, String totalAmount){
        Map<String, Object> totalData = new HashMap<>();
        totalData.put("sl","");
        totalData.put("exchangeCode","");
        totalData.put("uploadDateTime", "");
        totalData.put("fileName", CommonService.generateClassForText("Total","fw-bold"));
        totalData.put("cocCount", CommonService.generateClassForText(String.valueOf(totalCocCount),"fw-bold"));
        totalData.put("beftnCount", CommonService.generateClassForText(String.valueOf(totalBeftnCount),"fw-bold"));
        totalData.put("onlineCount", CommonService.generateClassForText(String.valueOf(totalOnlineCount),"fw-bold"));
        totalData.put("accountPayeeCount", CommonService.generateClassForText(String.valueOf(totalAccountPayeeCount),"fw-bold"));
        totalData.put("errorCount", CommonService.generateClassForText(String.valueOf(totalErrorCount),"fw-bold"));
        totalData.put("totalCount", CommonService.generateClassForText(String.valueOf(totalCount),"fw-bold"));
        totalData.put("totalAmount", CommonService.generateClassForText(totalAmount, "fw-bold"));
        totalData.put("action", "");
        return totalData;
    }

    public Map<String, Object> calculateExchangeWiseSummary(List<Map<String,Object>> exchangeData, String previousExchangeCode){
        String[] summaryFields = {"sl","action", "uploadDateTime"};
        Map<String, Object> exchangeSummary = new HashMap<>();
        for(Map<String, Object> exData: exchangeData){
            if(exData.get("exchange_code").equals(previousExchangeCode)){
                exchangeSummary.put("exchangeCode", exData.get("exchange_code"));
                exchangeSummary.put("totalAmount", CommonService.generateClassForText(exData.get("totalAmount").toString(),"fw-bold text-success"));
                exchangeSummary.put("onlineCount", CommonService.generateClassForText(exData.get("onlineCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("accountPayeeCount", CommonService.generateClassForText(exData.get("accountPayeeCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("beftnCount", CommonService.generateClassForText(exData.get("beftnCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("cocCount", CommonService.generateClassForText(exData.get("cocCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("errorCount", CommonService.generateClassForText(exData.get("errorCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("totalCount", CommonService.generateClassForText(exData.get("totalCount").toString(),"fw-bold text-success"));
                exchangeSummary.put("fileName", CommonService.generateClassForText("Total","fw-bold text-success"));
                for(String field: summaryFields)    exchangeSummary.put(field, "");
            }else continue;
        }
        return exchangeSummary;
    }
    //1- transactionNo, 2- benificiaryNo
    public Map<String, Object> getSearch(String searchType, String searchValue){
        Map<String, Object> resp = new HashMap<>();
        List<ReportModel> reportModelList = new ArrayList<>();
        switch(searchType){
            case "1":
                reportModelList = reportModelRepository.findReportModelByTransactionNo(searchValue);
                break;
            case "2":
                reportModelList = reportModelRepository.findReportModelByBeneficiaryAccount(searchValue);
                break;
        }
        if(searchType.isEmpty() || searchValue.isEmpty())     return CommonService.getResp(1, "Please Select Search Type or Value", null);
        if(reportModelList.isEmpty()){
            //search 4 tables for live data if empty
            List<OnlineModel> onlineModelList = onlineModelService.getDataByTransactionNoOrBenificiaryAccount(searchType, searchValue);
            if(!onlineModelList.isEmpty()){
                resp = CommonService.getResp(0, "", processSearchData(onlineModelList, "1"));
                return resp;
            }
            List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getDataByTransactionNoOrBenificiaryAccount(searchType, searchValue);
            if(!accountPayeeModelList.isEmpty()){
                resp = CommonService.getResp(0, "", processSearchData(accountPayeeModelList, "2"));
                return resp;
            }
            List<BeftnModel> beftnModelList = beftnModelService.getDataByTransactionNoOrBenificiaryAccount(searchType, searchValue);
            if(!beftnModelList.isEmpty()){
                resp = CommonService.getResp(0, "", processSearchData(beftnModelList, "3"));
                return resp;
            }
            List<CocModel> cocModelList = cocModelService.getDataByTransactionNoOrBenificiaryAccount(searchType, searchValue);
            if(!cocModelList.isEmpty()){
                resp = CommonService.getResp(0, "", processSearchData(cocModelList, "4"));
                return resp;
            }
            return CommonService.getResp(1, "No data found", null);
        }
        resp = CommonService.getResp(0, "", processSearchData(reportModelList, ""));
        return resp;
    }

    public <T> List<Map<String, Object>> processSearchData(List<T> modelList, String type){
        List<Map<String, Object>> dataList = new ArrayList<>();
        if(modelList != null && !modelList.isEmpty()){
            int i = 1;
            Map<String, Object> remType = CommonService.getRemittanceTypes();

            for(T model: modelList){
                try{
                    Map<String, Object> data = new HashMap<>();
                    String action = "";
                    String typeFlag = (("").equals(type)) ? (String) CommonService.getPropertyValue(model, "getType") : type;
                    data.put("sl", i++);
                    data.put("transactionNo", (String) CommonService.getPropertyValue(model, "getTransactionNo"));
                    data.put("exchangeCode", (String) CommonService.getPropertyValue(model, "getExchangeCode"));
                    data.put("beneficiaryName", (String) CommonService.getPropertyValue(model, "getBeneficiaryName"));
                    data.put("beneficiaryAccount", (String) CommonService.getPropertyValue(model, "getBeneficiaryAccount"));
                    
                    String branchCode = (("3").equals(type)) ? "getRoutingNo": "getBranchCode";
                    String bankDetails = "Bank Name:" + (String) CommonService.getPropertyValue(model, "getBankName") + "<br>Branch Code/ Routing No: " 
                    + (String) CommonService.getPropertyValue(model, branchCode) + "<br> Branch Name: " + (String) CommonService.getPropertyValue(model, "getBranchName"); 
                    data.put("bankDetails", bankDetails);
                    //data.put("bankName", (String) CommonService.getPropertyValue(model, "getBankName"));
                    //data.put("branchCode", (String) CommonService.getPropertyValue(model, branchCode));
                    //data.put("branchName", (String) CommonService.getPropertyValue(model, "getBranchName"));
                    data.put("amount", (Double) CommonService.getPropertyValue(model, "getAmount"));
                    LocalDateTime downloaDateTime = (LocalDateTime) CommonService.getPropertyValue(model, "getDownloadDateTime");
                    String downloadDate = CommonService.convertDateToString(downloaDateTime);
                    data.put("downloadDateTime", downloadDate);
                    data.put("reportDate", (LocalDate) CommonService.getPropertyValue(model, "getReportDate"));
                    data.put("type", remType.get(typeFlag));
                    dataList.add(data);
                }catch(Exception e){
                    e.printStackTrace();
                    //CommonService.getResp(1, "Error processing model " + e.getMessage(), null);
                }  
            }
        }
        return dataList;
    }

    public Map<String, Object> deleteByFileInfoModelById(int id, int userId, HttpServletRequest request){
        Map<String, Object> resp = new HashMap<>();
        if(id == 0) return CommonService.getResp(1, "Please select id", null);
        String pmsg = "Data has processed from this file. You can't delete it";
        FileInfoModel fileInfoModel = fileInfoModelService.findFileInfoModelById(id);
        if(fileInfoModel == null)   return CommonService.getResp(1, "No data found following this fileInfoModel Id", null);
        int cnt = 0;
        if(CommonService.convertStringToInt(fileInfoModel.getOnlineCount()) >= 1){
            List<OnlineModel> onlineModels = onlineModelService.findOnlineModelByFileInfoModelIdAndIsDownloaded(id, fileInfoModel.getIsSettlement());
            cnt += onlineModels.size();
        }
        if(CommonService.convertStringToInt(fileInfoModel.getAccountPayeeCount()) >= 1){
            List<AccountPayeeModel> accountPayeeModels = accountPayeeModelService.findAccountPayeeModelByFileInfoModelIdAndIsDownloaded(id);
            cnt += accountPayeeModels.size();
        }
        if(CommonService.convertStringToInt(fileInfoModel.getBeftnCount()) >= 1){
            List<BeftnModel> beftnModels = beftnModelService.findBeftnModelByFileInfoModelIdAndIsDownloaded(id);
            cnt += beftnModels.size();
        }
        if(CommonService.convertStringToInt(fileInfoModel.getCocCount()) >= 1){
            if(fileInfoModel.getIsSettlement() == 1){
                List<CocPaidModel> cocPaidModels = cocPaidModelService.findCocPaidModelByFileInfoModelIdAndIsVoucherGenerated(id);
                cnt += cocPaidModels.size();
            }else{
                List<CocModel> cocModels = cocModelService.findCocModelByFileInfoModelIdAndIsDownloaded(id);
                cnt += cocModels.size();
            }
            
        }
        if(fileInfoModel.getErrorCount() >= 1){
            List<ErrorDataModel> errorDataModels = errorDataModelRepository.getErrorSubmittedByFileInfoModelId(id);
            cnt += errorDataModels.size();
        }

        if(cnt > 0) return CommonService.getResp(1, pmsg, null);
        resp = fileInfoModelService.deleteFileInfoModel(fileInfoModel);
        if((Integer) resp.get("err") == 0){
            Map<String, Object> info = new HashMap<>();
            info.put("fileInfoModel", fileInfoModel);
            String exchangeCode = fileInfoModel.getExchangeCode();
            Map<String, Object> logResp = logModelService.addLogModel(userId, id, exchangeCode, "", "3", info, request);
            if((Integer) logResp.get("err") == 1)   return logResp;
        }
        return resp;
    }
    

    public Map<String, Object> getExchangeWiseData(String date, int userId){
        Map<String, Object> resp = new HashMap<>();
        Map<String, LocalDateTime> dateTime = CommonService.getStartAndEndDateTime(date);
        LocalDateTime starDateTime = (LocalDateTime) dateTime.get("startDateTime");
        LocalDateTime enDateTime = (LocalDateTime) dateTime.get("endDateTime");
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
        return resp;
    }

   //------------------------- Below Methods for getting data from Report Table for generating MO -----------------------
    public List<Object> getAllBeftnSummaryForMo(LocalDate date){
        Object[] result = reportModelRepository.getAllBeftnSummaryForMo(date);
        return getSummary(result);
    }
    public List<Object> getAllOtherSummaryForMo(LocalDate date){
        Object[] result = reportModelRepository.getAllOtherSummaryForMo(date);
        return getSummary(result);
    }
    public List<Object> getAllOnlineSummaryForMo(LocalDate date){
        Object[] result = reportModelRepository.getAllOnlineSummaryForMo(date);
        return getSummary(result);
    }
    public List<Object> getAllApiSummaryForMo(LocalDate date){
        Object[] result = reportModelRepository.getAllApiSummaryForMo(date);
        return getSummary(result);
    }
    private List<Object> getSummary(Object[] result) {
        List<Object> summary = new ArrayList<>();
        if (result != null && result.length == 1 && result[0] instanceof Object[]) {
            Object[] innerResult = (Object[]) result[0]; // Extract the nested Object[]
            Long totalRows = innerResult[0] instanceof Number ? ((Number) innerResult[0]).longValue() : 0L;
            Double totalAmount = innerResult[1] instanceof Number ? ((Number) innerResult[1]).doubleValue() : 0.0;

            summary.add(totalRows);
            summary.add(totalAmount);
        }
        return summary;
    }
    // ---------------------------- End of Methods Block for getting data from Report Table for generating MO -------------------------------

}
