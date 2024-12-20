package abl.frd.qremit.converter.nafex.service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
        Path reportPath = CommonService.getReportFile(CommonService.generateFileName("summary_report_", date, ".pdf"));
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
        Path reportPath = CommonService.getReportFile(CommonService.generateFileName("daily_voucher_", date, ".pdf"));
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
        Path reportPath = CommonService.getReportFile(CommonService.generateFileName("details_report_", date, "." + format.toLowerCase()));
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
                    reportModel.setReportDate(currentDate);
                    reportModel.setType(types);
                    reportModel.setDataModelId(id);
                    //reportModelRepository.save(reportModel);
                    //setIsVoucherGenerated(types, id, currentDateTime);
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
        FileInfoModel fileInfoModel = fileInfoModelService.findAllById(id);
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
    public Map<String, Object> calculateTotalUploadFileInfo(int totalCocCount, int totalBeftnCount, int totalOnlineCount, int totalAccountPayeeCount, int totalErrorCount, int totalCount){
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
        totalData.put("action", "");
        return totalData;
    }

}
