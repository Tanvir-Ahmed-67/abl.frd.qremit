package abl.frd.qremit.converter.service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    DynamicOperationService dynamicOperationService;

    DateTimeFormatter yyMMddFormatter = DateTimeFormatter.ofPattern("yyMMdd");     // YYMMDD
    DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // YYYY/MM/DD
    DateTimeFormatter ddMMyyyyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // DD/MM/YYYY
    
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
        //if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        //}
        
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public byte[] generateDailyVoucherInPdfFormat(List<ExchangeReportDTO> dataList, String date) throws Exception {
        LocalDate currentDate = LocalDate.now();
        // Collect all unique exchange codes from dataList
        Set<String> exchangeCodes = dataList.stream()
                .map(ExchangeReportDTO::getExchangeCode)
                .collect(Collectors.toSet());
        // Fetch all ExchangeHouseModels for these exchange codes in one go
        Map<String, ExchangeHouseModel> exchangeHouseMap = exchangeHouseModelService.findAllByExchangeCodeIn(exchangeCodes).stream().collect(Collectors.toMap(ExchangeHouseModel::getExchangeCode, Function.identity()));
        for(int i =0; i<dataList.size();i++){
            dataList.get(i).setExchangeName(exchangeHouseMap.get(dataList.get(i).getExchangeCode()).getExchangeName());
            dataList.get(i).setNrtAccountNo(exchangeHouseMap.get(dataList.get(i).getExchangeCode()).getNrtaCode());
            dataList.get(i).setEnteredDate(currentDate);
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
        ///if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        //}
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public List<ExchangeReportDTO> getAllDailyReportData(String date){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate reportDate = LocalDate.parse(date);
        List<ReportModel> reportModelsList = reportModelRepository.getReportModelByReportDate(reportDate);
        if(isListValid(reportModelsList)){
            for(ReportModel reportModel:reportModelsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setEnteredDate(reportModel.getDownloadDateTime().toLocalDate());
                report.add(exchangeReportDTO);
            }
        }
        return report;
    }
    public List<ExchangeReportDTO> generateDetailsOfDailyRemittances(String fromDate, String toDate) {
        List<ExchangeReportDTO> exchangeReportDTOSList = getAllDailyReportDataByDateRange(fromDate, toDate);
        return exchangeReportDTOSList;
    }
    public Map<String, Object> generateDetailsOfDailyCocOnlineAndAccountPayeeData(String fromDate, String toDate) {
        List<ExchangeReportDTO> exchangeReportDTOSList = getAllCocOnlineAndAccountPayeeDataDataByDateRange(fromDate, toDate);
        Map<String, Object> resp;
        resp = CommonService.getResp(0,"", null);
        resp.put("data", exchangeReportDTOSList);
        return resp;
    }

    public Map<String, Object> generateDetailsOfDailyStatement(String fromDate, String toDate) {
        List<ExchangeReportDTO> exchangeReportDTOSList = getAllDailyReportDataByDateRange(fromDate, toDate);
        Map<String, Object> resp;
        resp = CommonService.getResp(0,"", null);
        resp.put("data", exchangeReportDTOSList);
        return resp;
    }
    public List<ExchangeReportDTO> getAllDailyReportDataByDateRange(String fromDate, String toDate){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate fDate = LocalDate.parse(fromDate);
        LocalDate tDate = LocalDate.parse(toDate);
        List<ReportModel> reportModelsList = reportModelRepository.getReportModelByReportDateRange(fDate, tDate);
        if(isListValid(reportModelsList)){
            List<ReportModel> sortedReportsList = reportModelsList.stream()
                    .sorted(Comparator.comparing(ReportModel::getExchangeCode))
                    .collect(Collectors.toList());
            int counter = 1;
            LocalDate formattedDate;
            for(ReportModel reportModel:sortedReportsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setTotalRowCount(counter);
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setGovtIncentive(reportModel.getGovtIncentive());
                exchangeReportDTO.setAgraniIncentive(reportModel.getAgraniIncentive());
                exchangeReportDTO.setIncentive(reportModel.getIncentive());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setBankCode(reportModel.getBankCode());
                exchangeReportDTO.setBankName(reportModel.getBankName());
                exchangeReportDTO.setBranchCode(reportModel.getBranchCode());
                exchangeReportDTO.setBranchName(reportModel.getBranchName());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setMoNumber(reportModel.getMoNumber());

                // Block for setting different date pattern for different exchange houses for swift operation.
                String enteredDateStr = reportModel.getEnteredDate();
                if(enteredDateStr == null || enteredDateStr.trim().isEmpty()){
                    formattedDate = null;
                }else{
                    formattedDate = CommonService.convertStringToLocalDate(enteredDateStr,"yyyy-MM-dd");
                }
                //if (enteredDateStr == null || enteredDateStr.trim().isEmpty()) {
                if(formattedDate == null){
                    formattedDate = LocalDate.now();
                }
                String exchangeCode = reportModel.getExchangeCode();
                String pattern;

                // Assign pattern based on exchangeCode
                switch (exchangeCode) {
                    case "7119":       // Ajraji
                        exchangeReportDTO.setExchangeCode("7009");
                    case "7010204":    // Bilad
                        pattern = "yyMMdd";
                        break;

                    case "7010203":    // Anb
                        pattern = "yyyy/MM/dd";
                        break;

                    case "7010241":    // Ncb
                    case "7010299":    // BFC UK
                        pattern = "dd/MM/yyyy";
                        break;

                    default:
                        pattern = "dd/MM/yyyy";
                        break;
                }
                exchangeReportDTO.setEnteredDateForSearchFile(CommonService.convertLocalDateToString(formattedDate,pattern));
                exchangeReportDTO.setVoucherDate(reportModel.getReportDate());
                report.add(exchangeReportDTO);
                counter++;
            }
        }
        return report;
    }
    public List<ExchangeReportDTO> getAllCocOnlineAndAccountPayeeDataDataByDateRange(String fromDate, String toDate){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate fDate = LocalDate.parse(fromDate);
        LocalDate tDate = LocalDate.parse(toDate);
        List<ReportModel> reportModelsList = reportModelRepository.getAllCocOnlineAndAccountPayeeDataDataByDateRange(fDate, tDate);
        if(isListValid(reportModelsList)){
            List<ReportModel> sortedReportsList = reportModelsList.stream()
                    .sorted(Comparator.comparing(ReportModel::getExchangeCode))
                    .collect(Collectors.toList());
            int counter = 1;
            for(ReportModel reportModel:sortedReportsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setTotalRowCount(counter);
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setGovtIncentive(reportModel.getGovtIncentive());
                exchangeReportDTO.setAgraniIncentive(reportModel.getAgraniIncentive());
                exchangeReportDTO.setIncentive(reportModel.getIncentive());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setBankCode(reportModel.getBankCode());
                exchangeReportDTO.setBankName(reportModel.getBankName());
                exchangeReportDTO.setBranchCode(reportModel.getBranchCode());
                exchangeReportDTO.setBranchName(reportModel.getBranchName());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setMoNumber(reportModel.getMoNumber());
                exchangeReportDTO.setEnteredDateForSearchFile(reportModel.getEnteredDate());
                exchangeReportDTO.setVoucherDate(reportModel.getReportDate());
                exchangeReportDTO.setType(reportModel.getType());
                report.add(exchangeReportDTO);
                counter++;
            }
        }
        return report;
    }
    public List<ExchangeReportDTO> getAllCocAndAccountPayeeDataByDateRange(String fromDate, String toDate){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate fDate = LocalDate.parse(fromDate);
        LocalDate tDate = LocalDate.parse(toDate);
        List<ReportModel> reportModelsList = reportModelRepository.getAllCocAndAccountPayeeDataByDateRange(fDate, tDate);
        if(isListValid(reportModelsList)){
            List<ReportModel> sortedReportsList = reportModelsList.stream()
                    .sorted(Comparator.comparing(ReportModel::getExchangeCode))
                    .collect(Collectors.toList());
            int counter = 1;
            for(ReportModel reportModel:sortedReportsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setTotalRowCount(counter);
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setGovtIncentive(reportModel.getGovtIncentive());
                exchangeReportDTO.setAgraniIncentive(reportModel.getAgraniIncentive());
                exchangeReportDTO.setIncentive(reportModel.getIncentive());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setBankCode(reportModel.getBankCode());
                exchangeReportDTO.setBankName(reportModel.getBankName());
                exchangeReportDTO.setBranchCode(reportModel.getBranchCode());
                exchangeReportDTO.setBranchName(reportModel.getBranchName());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setMoNumber(reportModel.getMoNumber());
                exchangeReportDTO.setEnteredDateForSearchFile(reportModel.getEnteredDate());
                exchangeReportDTO.setVoucherDate(reportModel.getReportDate());
                exchangeReportDTO.setType(reportModel.getType());
                report.add(exchangeReportDTO);
                counter++;
            }
        }
        return report;
    }
    public List<ExchangeReportDTO> getAllOnlineDataByDateRange(String fromDate, String toDate){
        List<ExchangeReportDTO> report = new ArrayList<>();
        LocalDate fDate = LocalDate.parse(fromDate);
        LocalDate tDate = LocalDate.parse(toDate);
        List<ReportModel> reportModelsList = reportModelRepository.getAllOnlineDataByDateRange(fDate, tDate);
        if(isListValid(reportModelsList)){
            List<ReportModel> sortedReportsList = reportModelsList.stream()
                    .sorted(Comparator.comparing(ReportModel::getExchangeCode))
                    .collect(Collectors.toList());
            int counter = 1;
            for(ReportModel reportModel:sortedReportsList){
                ExchangeReportDTO exchangeReportDTO = new ExchangeReportDTO();
                exchangeReportDTO.setTotalRowCount(counter);
                exchangeReportDTO.setExchangeCode(reportModel.getExchangeCode());
                exchangeReportDTO.setTransactionNo(reportModel.getTransactionNo());
                exchangeReportDTO.setAmount(reportModel.getAmount());
                exchangeReportDTO.setGovtIncentive(reportModel.getGovtIncentive());
                exchangeReportDTO.setAgraniIncentive(reportModel.getAgraniIncentive());
                exchangeReportDTO.setIncentive(reportModel.getIncentive());
                exchangeReportDTO.setBeneficiaryName(reportModel.getBeneficiaryName());
                exchangeReportDTO.setBeneficiaryAccount(reportModel.getBeneficiaryAccount());
                exchangeReportDTO.setBankCode(reportModel.getBankCode());
                exchangeReportDTO.setBankName(reportModel.getBankName());
                exchangeReportDTO.setBranchCode(reportModel.getBranchCode());
                exchangeReportDTO.setBranchName(reportModel.getBranchName());
                exchangeReportDTO.setRemitterName(reportModel.getRemitterName());
                exchangeReportDTO.setMoNumber(reportModel.getMoNumber());
                exchangeReportDTO.setEnteredDateForSearchFile(reportModel.getEnteredDate());
                exchangeReportDTO.setVoucherDate(reportModel.getReportDate());
                exchangeReportDTO.setType(reportModel.getType());
                report.add(exchangeReportDTO);
                counter++;
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
        dto.setEnteredDate(onlineModel.getFileInfoModel().getUploadDateTime().toLocalDate());
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
        dto.setEnteredDate(beftnModel.getFileInfoModel().getUploadDateTime().toLocalDate());
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
        dto.setEnteredDate(cocModel.getFileInfoModel().getUploadDateTime().toLocalDate());
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
        dto.setEnteredDate(accountPayeeModel.getFileInfoModel().getUploadDateTime().toLocalDate());
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
                    reportModel.setGovtIncentive((Double) CommonService.getPropertyValue(model, "getGovtIncentive"));
                    reportModel.setAgraniIncentive((Double) CommonService.getPropertyValue(model, "getAgraniIncentive"));
                    reportModel.setIncentive((Double) CommonService.getPropertyValue(model, "getIncentive"));
                    reportModel.setUploadDateTime((LocalDateTime) CommonService.getPropertyValue(model, "getUploadDateTime"));
                    reportModel.setReportDate(currentDate);
                    reportModel.setType(types);
                    reportModel.setDataModelId(id);
                    reportModel.setEnteredDate((String) CommonService.getPropertyValue(model, "getEnteredDate"));
                    if(("1").equals(types) || ("2").equals(types)) reportModel.setIsApi((Integer) CommonService.getPropertyValue(model, "getIsApi"));
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
        return processSearchData(modelList, type, 0);
    }

    public <T> List<Map<String, Object>> processSearchData(List<T> modelList, String type, int isEdit){
        List<Map<String, Object>> dataList = new ArrayList<>();
        if(modelList != null && !modelList.isEmpty()){
            int i = 1;
            Map<String, Object> remType = CommonService.getRemittanceTypes();

            for(T model: modelList){
                try{
                    Map<String, Object> data = new HashMap<>();
                    int id = (Integer) CommonService.getPropertyValue(model, "getId");
                    String action = "";
                    String typeFlag = (("").equals(type)) ? (String) CommonService.getPropertyValue(model, "getType") : type;
                    if(isEdit == 1){
                        String btn = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm edit",String.valueOf(id),"Edit");
                        btn += "<input type='hidden' id='type_" + id + "' value='" + typeFlag + "' />";
                        btn += CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm delete",String.valueOf(id),"Delete");
                        action = CommonService.generateTemplateBtn("template-btngroup.txt", "#", "", "", btn);
                    }
                    
                    data.put("sl", i++);
                    data.put("transactionNo", (String) CommonService.getPropertyValue(model, "getTransactionNo"));
                    data.put("exchangeCode", (String) CommonService.getPropertyValue(model, "getExchangeCode"));
                    data.put("beneficiaryName", (String) CommonService.getPropertyValue(model, "getBeneficiaryName"));
                    data.put("beneficiaryAccount", (String) CommonService.getPropertyValue(model, "getBeneficiaryAccount"));
                    
                    String branchCodeMethod = (("3").equals(type)) ? "getRoutingNo": "getBranchCode";
                    String bankName = (String) CommonService.getPropertyValue(model, "getBankName");
                    String branchCode = (String) CommonService.getPropertyValue(model, branchCodeMethod);
                    String branchName = (String) CommonService.getPropertyValue(model, "getBranchName");
                    String bankDetails = "Bank Name:" + bankName + "<br>Branch Code/ Routing No: " + branchCode + "<br> Branch Name: " + branchName; 
                    data.put("bankDetails", bankDetails);
                    data.put("bankName", bankName);
                    data.put("branchCode", branchCode);
                    data.put("branchName", branchName);
                    data.put("amount", (Double) CommonService.getPropertyValue(model, "getAmount"));
                    LocalDateTime downloaDateTime = (LocalDateTime) CommonService.getPropertyValue(model, "getDownloadDateTime");
                    String downloadDate = CommonService.convertDateToString(downloaDateTime);
                    data.put("downloadDateTime", downloadDate);
                    data.put("reportDate", (LocalDate) CommonService.getPropertyValue(model, "getReportDate"));
                    data.put("type", remType.get(typeFlag));
                    data.put("typeFlag", typeFlag);
                    data.put("action", action);
                    dataList.add(data);
                }catch(Exception e){
                    e.printStackTrace();
                    CommonService.getResp(1, "Error processing model " + e.getMessage(), null);
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
        Map<String, Object> info = new HashMap<>();
        info.put("fileInfoModel", fileInfoModel);
        if(CommonService.convertStringToInt(fileInfoModel.getTotalCount()) == 0 && fileInfoModel.getErrorCount() == 0){
            //for not file uploaded
            resp = fileInfoModelService.deleteFileInfoModelById(id);
            resp = addDataLogModel(resp, fileInfoModel, userId, id, request, "", "3", info);
            return resp;
        }
        if(CommonService.convertStringToInt(fileInfoModel.getTotalCount()) == 0 && fileInfoModel.getErrorCount() > 0){
            //file only has error but no data
            resp = fileInfoModelService.deleteFileInfoModelHavingOnlyErrors(fileInfoModel);
            
            resp = addDataLogModel(resp, fileInfoModel, userId, id, request, "", "3", info);
            return resp;
        }
        if(CommonService.convertStringToInt(fileInfoModel.getOnlineCount()) >= 1){
            List<OnlineModel> onlineModels = onlineModelService.findOnlineModelByFileInfoModelIdAndIsDownloaded(id, fileInfoModel.getIsSettlement());
            cnt += onlineModels.size();
        }
        if(CommonService.convertStringToInt(fileInfoModel.getAccountPayeeCount()) >= 1){
            List<AccountPayeeModel> accountPayeeModels = accountPayeeModelService.findAccountPayeeModelByFileInfoModelIdAndIsDownloaded(id, fileInfoModel.getIsSettlement());
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
        if((Integer) resp.get("err") == 1)  return resp;
        resp = addDataLogModel(resp, fileInfoModel, userId, id, request, "", "3", info);
        return resp;
    }

    public Map<String, Object> addDataLogModel(Map<String, Object> resp, FileInfoModel fileInfoModel, int userId, int id, HttpServletRequest request, String dataId, String action, Map<String, Object> info){
        if((Integer) resp.get("err") == 0){
            String exchangeCode = fileInfoModel.getExchangeCode();
            Map<String, Object> logResp = logModelService.addLogModel(userId, id, exchangeCode, dataId, action, info, request);
            if((Integer) logResp.get("err") == 1)   return logResp;
        }
        return resp;
    }
    
    public Map<String, Object> getCorrectionSearch(String searchType, String searchValue){
        if(searchType.isEmpty() || searchValue.isEmpty())     return CommonService.getResp(1, "Please Select Search Type or Value", null);
        List<OnlineModel> onlineModelList = onlineModelService.getOnlineModelByTransactionNoAndIsDownloaded(searchValue, 0);
        if(!onlineModelList.isEmpty()){
            return CommonService.getResp(0, "", processSearchData(onlineModelList, "1",1));
        }
        List<AccountPayeeModel> accountPayeeModelList = accountPayeeModelService.getAccountPayeeModelByTransactionNoAndIsDownloaded(searchValue, 0);
        if(!accountPayeeModelList.isEmpty()){
            return CommonService.getResp(0, "", processSearchData(accountPayeeModelList, "2", 1));
        }
        List<BeftnModel> beftnModelList = beftnModelService.getBeftnModelByTransactionNoAndIsDownloaded(searchValue, 0);
        if(!beftnModelList.isEmpty()){
            return CommonService.getResp(0, "", processSearchData(beftnModelList, "3", 1));
        }
        List<CocModel> cocModelList = cocModelService.getCoCModelByTransactionNoAndIsDownloaded(searchValue, 0);
        if(!cocModelList.isEmpty()){
            return CommonService.getResp(0, "", processSearchData(cocModelList, "4", 1));
        }
        return CommonService.getResp(1, "No data found for edit", null);
    }

    public Map<String, Object> getEditData(int id, String type, int convertObj){
        Map<String, Object> resp = new HashMap<>();
        if(id == 0 || ("").equals(type))    return CommonService.getResp(1, "Please select id or type", null);
        String msg = "No data found using following Id for edit";
        switch (type) {
            case "1":
                OnlineModel onlineModel = onlineModelService.findOnlineModelByIdAndIsDownloaded(id, 0);
                if(onlineModel == null)   return CommonService.getResp(1, msg, null);
                resp = CommonService.getResp(0, "", null);
                if(convertObj == 1) resp.put("data", CommonService.convertModelToObject(onlineModel));
                else resp.put("data", onlineModel);
                return resp;
            case "2":
                AccountPayeeModel accountPayeeModel = accountPayeeModelService.findAccountPayeeModelIdAndIsDownloaded(id, 0);
                if(accountPayeeModel == null)   return CommonService.getResp(1, msg, null);
                resp = CommonService.getResp(0, "", null);
                if(convertObj == 1) resp.put("data", CommonService.convertModelToObject(accountPayeeModel));
                else resp.put("data", accountPayeeModel);
                return resp;
            case "3":
                BeftnModel beftnModel = beftnModelService.findBeftnModelByIdAndIsDownloaded(id, 0);
                if(beftnModel == null)   return CommonService.getResp(1, msg, null);
                resp = CommonService.getResp(0, "", null);
                Map<String, Object> data = CommonService.convertModelToObject(beftnModel);
                data.put("branchCode", data.get("routingNo")); //for beftn branchCode will be routingNo
                data.remove("routingNo");
                if(convertObj == 0){
                    data.remove("fileInfoModel");
                    data.remove("userModel");
                }
                resp.put("data", data);
                //if(convertObj == 1) resp.put("data", CommonService.convertModelToObject(beftnModel));
                //resp.put("data", beftnModel);
                return resp;
            case "4":
                CocModel cocModel = cocModelService.findCocModelByIdAndIsDownloaded(id, 0);
                if(cocModel == null)    return CommonService.getResp(1, msg, null);
                resp = CommonService.getResp(0, "", null);
                if(convertObj == 1) resp.put("data", CommonService.convertModelToObject(cocModel));
                else resp.put("data", cocModel);
                return resp;
            default:
                return CommonService.getResp(1, "Invalid Type Selected", null);
        }
    }

    public Map<String, Object> updateIndividualDataById(@RequestParam Map<String, String> formData, int userId, HttpServletRequest request) throws Exception{
        Map<String, Object> resp = new HashMap<>();
        String type = formData.get("type").toString();
        int id = CommonService.convertStringToInt(formData.get("id").toString());
        String bankName = formData.get("bankName").trim();
        String beneficiaryAccount =formData.get("beneficiaryAccount").trim();
        String beneficiaryName = formData.get("beneficiaryName").trim();
        String branchCode = formData.get("branchCode").trim();
        String amount = formData.get("amount").toString();
        String exchangeCode = formData.get("exchangeCode").toString();
        String transactionNo = formData.get("transactionNo").toString();
        
        String errorMessage = "";
        errorMessage = CommonService.getErrorMessage(beneficiaryAccount, beneficiaryName, amount, bankName, branchCode);
        if(!errorMessage.isEmpty())  return CommonService.getResp(1, errorMessage, null);
        resp = getEditData(id, type, 1);
        if((Integer) resp.get("err") == 1)  return resp;
        Map<String, Object> obj = (Map<String, Object>) resp.get("data");
        if(obj.containsKey("err") && ((Integer) obj.get("err") == 1))   return obj;
        FileInfoModel fileInfoModel = (FileInfoModel) obj.get("fileInfoModel");
        User user = (User) obj.get("userModel");
        if(!obj.get("transactionNo").equals(transactionNo) || !CommonService.convertStringToDouble(obj.get("amount").toString()).equals(CommonService.convertStringToDouble(amount))){
            errorMessage = "Amount or Transaction No mismatched";
            return CommonService.getResp(1, errorMessage, null);
        }
        String typeFlag = CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode);
        resp = dynamicOperationService.updateIndividualDataById(exchangeCode, fileInfoModel, user, transactionNo, formData, type, obj, typeFlag);
        if((Integer) resp.get("err") == 0){
            Map<String, Object> info = (Map<String, Object>) resp.get("data");
            resp = addDataLogModel(resp, fileInfoModel, userId, fileInfoModel.getId(), request, String.valueOf(id), "4", info);
        }
        return resp;
    }

    public Map<String, Object> deleteIndividualDataById(int id, int userId, String type, HttpServletRequest request){
        Map<String, Object> resp = getEditData(id, type, 1);
        if((Integer) resp.get("err") == 1)  return resp;
        Map<String, Object> obj = (Map<String, Object>) resp.get("data");
        FileInfoModel fileInfoModel = (FileInfoModel) obj.get("fileInfoModel");
        User user = (User) obj.get("userModel");
        int fileInfoModelId = fileInfoModel.getId();
        resp = dynamicOperationService.deleteIndividualDataById(obj, fileInfoModel, type);
        if((Integer) resp.get("err") == 1)  return resp;
        obj.put("userId", user.getId());
        obj.put("fileInfoModelId", fileInfoModelId);
        obj.remove("fileInfoModel");
        obj.remove("userModel");
        resp = addDataLogModel(resp, fileInfoModel, userId, fileInfoModelId, request, String.valueOf(id), "5", obj);
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

   //------------------------- Below Methods for getting data from Report Table for generating MO and updating MO Number in each row-----------------------
   @Transactional
    public List<Object> getAllBeftnSummaryForMo(String moNumber, LocalDate date){
        Object[] result = reportModelRepository.getAllBeftnSummaryForMo(date);
        reportModelRepository.updateMoNumberForAllBeftn(moNumber, date);
        return getSummary(result);
    }
    @Transactional
    public List<Object> getAllOtherSummaryForMo(String moNumber, LocalDate date){
        Object[] result = reportModelRepository.getAllOtherSummaryForMo(date);
        reportModelRepository.updateMoNumberForAllOther(moNumber, date);
        return getSummary(result);
    }
    @Transactional
    public List<Object> getAllOnlineSummaryForMo(String moNumber, LocalDate date){
        Object[] result = reportModelRepository.getAllOnlineSummaryForMo(date);
        reportModelRepository.updateMoNumberForAllOnline(moNumber, date);
        return getSummary(result);
    }
    @Transactional
    public List<Object> getAllApiSummaryForMo(String moNumber, LocalDate date){
        Object[] result = reportModelRepository.getAllApiSummaryForMo(date);
        reportModelRepository.updateMoNumberForAllApi(moNumber, date);
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
    public byte[] generateJasperSearchFileInPdfAndTxtFormat(List<ExchangeReportDTO> dataList, String format, String date) throws Exception {
        validateInputs(dataList, format, date);

        // Batch fetch exchange names
        enrichExchangeNames(dataList);

        // Generate report based on format
        switch (format.toLowerCase()) {
            case "pdf":
                return generateSearchInPdfFile(dataList, date);
            case "txt":
                return generateSearchInTxtFile(dataList, date);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private void validateInputs(List<ExchangeReportDTO> dataList, String format, String date) {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be null or empty.");
        }
        if (!"pdf".equalsIgnoreCase(format) && !"txt".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Invalid format: " + format);
        }
        LocalDate.parse(date); // Validates date format
    }

    private void enrichExchangeNames(List<ExchangeReportDTO> dataList) {
        Map<String, String> exchangeNames = exchangeHouseModelService.getExchangeNamesByCodes(
                dataList.stream().map(ExchangeReportDTO::getExchangeCode).collect(Collectors.toList())
        );
        dataList.forEach(dto -> dto.setExchangeName(exchangeNames.getOrDefault(dto.getExchangeCode(), "Unknown")));
    }

    private byte[] generateSearchInPdfFile(List<ExchangeReportDTO> dataList, String date) throws Exception {
        List<ExchangeReportDTO> modifiedList = new ArrayList<>(dataList);
        modifiedList.add(0, new ExchangeReportDTO()); // Avoid mutating original list
        Map<String, Object> parameters = prepareReportParameters(modifiedList, date);
        JasperReport jasperReport = loadJasperReport("search_pdf_tabular.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(modifiedList, false));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateSearchInTxtFile(List<ExchangeReportDTO> dataList, String date) throws Exception {
        Map<String, Object> parameters = prepareReportParameters(dataList, date);
        JasperReport jasperReport = loadJasperReport("search_csv.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(dataList, false));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            configureCsvExporter(exporter);
            exporter.exportReport();
            return outputStream.toByteArray();
        }
    }
    public byte[] generateCocAndAccountPayeeForBranchInTxtFile(List<ExchangeReportDTO> dataList, String date) throws Exception {
        Map<String, Object> parameters = prepareReportParameters(dataList, date);
        JasperReport jasperReport = loadJasperReport("cocAndAccPayeeForBranch_csv.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(dataList, false));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            configureCsvExporterForPipeDelimiter(exporter);
            exporter.exportReport();
            return outputStream.toByteArray();
        }
    }
    public byte[] generateOnlineForBranchInTxtFile(List<ExchangeReportDTO> dataList, String date) throws Exception {
        Map<String, Object> parameters = prepareReportParameters(dataList, date);
        JasperReport jasperReport = loadJasperReport("onlineForBranch_csv.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(dataList, false));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            configureCsvExporterForPipeDelimiter(exporter);
            exporter.exportReport();
            return outputStream.toByteArray();
        }
    }

    private Map<String, Object> prepareReportParameters(List<ExchangeReportDTO> dataList, String date) {
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList, false);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_DATA_SOURCE", dataSource);
        parameters.put("TO_DATE", LocalDate.parse(date));
        return parameters;
    }

    private void configureCsvExporter(JRCsvExporter exporter) {
        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        configuration.setFieldDelimiter(",");
        configuration.setForceFieldEnclosure(true);
        configuration.setRecordDelimiter("\r\n");
        exporter.setConfiguration(configuration);
    }
    private void configureCsvExporterForPipeDelimiter(JRCsvExporter exporter) {
        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        configuration.setFieldDelimiter("|");
        configuration.setForceFieldEnclosure(true);
        configuration.setRecordDelimiter("\r\n");
        exporter.setConfiguration(configuration);
    }
}
