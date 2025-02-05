package abl.frd.qremit.converter.controller;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import abl.frd.qremit.converter.model.*;
import abl.frd.qremit.converter.helper.NumberToWords;
import abl.frd.qremit.converter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import abl.frd.qremit.converter.helper.MyUserDetails;

@SuppressWarnings("unchecked")
@Controller
public class ReportController {

    private final MyUserDetailsService myUserDetailsService;
    private final FileInfoModelService fileInfoModelService;
    private final ReportService reportService;
    @Autowired
    CommonService commonService;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    LogModelService logModelService;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    MoModelService moModelService;
    @Autowired
    ReimbursementModelService reimbursementModelService;

    public ReportController(MyUserDetailsService myUserDetailsService,FileInfoModelService fileInfoModelService,ReportService reportService){
        this.myUserDetailsService = myUserDetailsService;
        this.fileInfoModelService = fileInfoModelService;
        this.reportService = reportService;
    }

    @GetMapping(value="/getReportColumn", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getReportColumnUrl(String type){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, String>> column = getReportColumn(type);
        resp.put("column", column);
        return resp;
    }

    public static List<Map<String, String>> getReportColumn(String type){
        String[] columnData = null;
        String[] columnTitles = null;
        switch(type){
            case "1":
            case "5":
            default:
                columnData = new String[] {"sl", "exchangeCode", "fileName", "cocCount", "beftnCount", "onlineCount", "accountPayeeCount", "totalCount", "errorCount", "totalAmount", "uploadDateTime", "action"};
                columnTitles = new String[] {"SL", "Exchange Code", "File Name", "COC", "BEFTN", "Online", "Account Payee", "Total Processed", "Total Error", "Total Amount", "Upload Date", "Action"};
                break;
            case "2":
                columnData = new String[] {"sl", "transactionNo", "exchangeCode", "beneficiaryName", "beneficiaryAccountNo", "bankName", "branchCode", "branchName","remitterName", "amount","processedDate","remType"};
                columnTitles = new String[] {"SL", "Transaction No", "Exchange Code", "Beneficiary Name", "Account No",  "Bank Name", "Routing No/ Branch Code", "Branch Name","Remitter Name","Amount","Processed Date","Type"};
                break;
            case "3":
            case "4":
                columnData = new String[] {"sl", "transactionNo", "exchangeCode", "beneficiaryName", "beneficiaryAccountNo", "bankName", "branchCode", "branchName","amount","uploadDateTime","errorMessage","action"};
                columnTitles = new String[] {"SL", "Transaction No", "Exchange Code", "Beneficiary Name", "Account No",  "Bank Name", "Routing No/ Branch Code", "Branch Name","Amount","Upload Date","Error Mesage","Action"};
                break;
            case "6":
            case "10":
                columnData = new String[] {"sl", "email", "userName", "role", "exchangeCode", "status", "action"};
                columnTitles = new String[] {"SL", "Email", "User Name", "Role", "Exchange Code","Status","Action"};
                break;
            case "7":
                columnData = new String[] {"sl", "exchangeName", "exchangeCode", "nrtaCode", "totalRemittance", "totalAmount"};
                columnTitles = new String[] {"SL", "Exchange Name", "Exchange Code", "NRTA Code", "Total Remittances","Total Amount"};
                break;
            case "8":
            case "9":
                columnData = new String[] {"sl", "exchangeCode", "exchangeName", "exchangeShortName", "nrtaCode", "status", "action"};
                columnTitles = new String[] {"SL", "Exchange Code", "Exchange Name", "Exchange Short Name", "NRTA Code", "Status","Action"};
                break;
        }
        return CommonService.createColumns(columnData, columnTitles);
    }
    
    @GetMapping(value="/report", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUploadedFileInfo(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam(defaultValue = "") String date,
    @RequestParam(defaultValue = "") String type){
        Map<String, Object> resp = new HashMap<>();
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        if(date.isEmpty()){
            date = currentDate;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        int userId = (int) userData.get("userid");
        String baseUrl = (userId == 0) ? "/adminReport": "/user-home-page";
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        List<FileInfoModel> fileInfoModel = fileInfoModelService.getUploadedFileDetails(userId, date);
        List<Map<String, Object>> dataList = new ArrayList<>();
        if(fileInfoModel.isEmpty())     return ResponseEntity.ok(CommonService.getResp(1, "No data found", dataList));
        int totalCount = 0;
        int totalCocCount = 0;
        int totalBeftnCount = 0;
        int totalOnlineCount = 0;
        int totalAccountPayeeCount = 0;
        int totalErrorCount = 0;
        Double totalAmount = 0.0;
        String previousExchangeCode = null;
        int sl = 1;
        String action = "";
        Map<String, Object> exchangeResp = customQueryService.getFileTotalExchangeWise(date, userId);
        if((Integer) exchangeResp.get("err") == 1)  return ResponseEntity.ok(exchangeResp);
        List<Map<String,Object>> exchangeData = (List<Map<String, Object>>) exchangeResp.get("data");
        String btn = "";
        for (FileInfoModel fModel : fileInfoModel) {
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> exchangeSummary = new HashMap<>();
            String currentExchangeCode = fModel.getExchangeCode();
            if(previousExchangeCode != null && !previousExchangeCode.equals(currentExchangeCode)){
                exchangeSummary = reportService.calculateExchangeWiseSummary(exchangeData, previousExchangeCode);
                if(exchangeSummary.containsKey("totalAmount"))  dataList.add(exchangeSummary);
            }
            int id = fModel.getId();
            
            btn = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round view_exchange", String.valueOf(id),"View");
            //delete button shows only for admin using current date data and type = 5 
            if(userId == 0 && ("5").equals(type) && date.equals(currentDate)) btn += CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm delete_file", String.valueOf(id),"Delete");
            action = CommonService.generateTemplateBtn("template-btngroup.txt", "#", "", "", btn);

            //action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round view_exchange", String.valueOf(fModel.getId()),"View");
            action += "<input type='hidden' id='exCode_" + id + "' value='" + fModel.getExchangeCode() + "' />";
            action += "<input type='hidden' id='base_url' value='" + baseUrl + "' />";
            int cocCount = CommonService.convertStringToInt(fModel.getCocCount());
            totalCocCount += cocCount;
            int beftnCount = CommonService.convertStringToInt(fModel.getBeftnCount());
            totalBeftnCount += beftnCount;
            int onlineCount = CommonService.convertStringToInt(fModel.getOnlineCount());
            totalOnlineCount += onlineCount;
            int accountPayeeCount = CommonService.convertStringToInt(fModel.getAccountPayeeCount());
            totalAccountPayeeCount += accountPayeeCount;
            int errorCount = fModel.getErrorCount();
            totalErrorCount += errorCount;
            String errorStr = CommonService.convertIntToString(errorCount);
            String totalError = (errorCount >= 1) ? CommonService.generateClassForText(errorStr, "text-danger fw-bold"): errorStr;
            int total = CommonService.convertStringToInt(fModel.getTotalCount());
            totalCount += total;
            totalAmount += CommonService.convertStringToDouble(fModel.getTotalAmount());
            dataMap.put("sl", sl++);
            dataMap.put("id", fModel.getId());
            dataMap.put("exchangeCode", fModel.getExchangeCode());
            dataMap.put("uploadDateTime", CommonService.convertDateToString(fModel.getUploadDateTime()));
            dataMap.put("fileName", fModel.getFileName());
            dataMap.put("cocCount", cocCount);
            dataMap.put("beftnCount", beftnCount);
            dataMap.put("onlineCount", onlineCount);
            dataMap.put("accountPayeeCount", accountPayeeCount);
            dataMap.put("errorCount", totalError);
            dataMap.put("totalCount", total);
            dataMap.put("totalAmount", fModel.getTotalAmount());
            dataMap.put("action", action);
            dataList.add(dataMap);
            previousExchangeCode = currentExchangeCode;
        }
        if(previousExchangeCode != null){
            Map<String, Object> exchangeSummary = reportService.calculateExchangeWiseSummary(exchangeData, previousExchangeCode);
            if(exchangeSummary.containsKey("totalAmount")) dataList.add(exchangeSummary);
        }
        String totalAmountStr = CommonService.convertNumberFormat(totalAmount, 2);
        Map<String, Object> totalData = reportService.calculateTotalUploadFileInfo(totalCocCount, totalBeftnCount, totalOnlineCount, totalAccountPayeeCount, totalErrorCount, totalCount, totalAmountStr);
        dataList.add(totalData);
        resp.put("data", dataList);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/fileReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFileDetails(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam String id,@RequestParam String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, String>> reportColumn = getReportColumn("2");
        List<String> columnDataList = new ArrayList<>();
        for(Map<String, String> column: reportColumn){
            columnDataList.add(column.get("data"));
        }
        String[] columnData = columnDataList.toArray(new String[0]);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        String tbl = CommonService.getBaseTableName(exchangeHouseModel.getBaseTableName());
        Map<String,Object> fileInfo = customQueryService.getFileDetails(tbl,id);
        if((Integer) fileInfo.get("err") == 1)  return ResponseEntity.ok(fileInfo);
        resp = reportService.getFileDetails(CommonService.convertStringToInt(id), fileInfo, columnData);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/errorReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getErrorReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model, 
        @RequestParam(defaultValue = "") String id){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        int fileInfoModelId = 0;
        if(!id.isEmpty())  fileInfoModelId = CommonService.convertStringToInt(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        String exchangeCode;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
            exchangeCode = user.getExchangeCode();
            List<Map<String, Object>> dataList = errorDataModelService.getErrorReport(userId, fileInfoModelId, exchangeCode);
            resp.put("data", dataList);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/getErrorUpdateReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getErrorUpdateReport(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        //model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = errorDataModelService.getErrorUpdateReport();
        resp.put("data", dataList);
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value="/summaryOfDailyStatement", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> generateSummaryOfDailyStatement(Model model, @RequestParam(defaultValue = "") String date) {
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        List<ExchangeReportDTO> exchangeReport = reportService.generateSummaryOfDailyStatement(date);
        Double grandTotalAmount = 0.00;
        String commaFormattedGrandTotalAmount="";
        int grandTotalRemittances=0;
        int i = 1;
        for(ExchangeReportDTO exchangeReportDTO: exchangeReport){
            Map<String, Object> dataMap = new HashMap<>();
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
            grandTotalAmount = grandTotalAmount+exchangeReportDTO.getTotalAmountCount();
            grandTotalRemittances = grandTotalRemittances+exchangeReportDTO.getTotalRowCount();
            commaFormattedGrandTotalAmount = exchangeReportDTO.formattedAmount.format(grandTotalAmount);
            dataMap.put("sl", i++);
            dataMap.put("exchangeCode", exchangeReportDTO.getExchangeCode());
            dataMap.put("nrtaCode", exchangeReportDTO.getNrtAccountNo());
            dataMap.put("exchangeName", exchangeReportDTO.getExchangeName());
            dataMap.put("totalRemittance", exchangeReportDTO.getTotalRowCount());
            dataMap.put("totalAmount", exchangeReportDTO.doFormatAmount(exchangeReportDTO.getTotalAmountCount()));
            dataList.add(dataMap);
        }
        if(!dataList.isEmpty()){
            Map<String, Object> totalData = calculateTotalSummaryOfDailyStatemen(commaFormattedGrandTotalAmount, String.valueOf(grandTotalRemittances));
            dataList.add(totalData);
            resp.put("dailyStatementUrl","/downloadSummaryOfDailyStatementInPdfFormat?date=" + date);
            resp.put("dailyStatementTitle","Download Summary in PDF");
            resp.put("dailyVoucherUrl","/downloaDailyVoucherInPdfFormat?date=" + date);
            resp.put("dailyVoucherTitle", "Download Voucher in PDF");
        }
        
        resp.put("data", dataList);
        return resp;
        /*
        model.addAttribute("summaryReportContent", exchangeReport);
        model.addAttribute("grandTotalAmount", commaFormattedGrandTotalAmount);
        model.addAttribute("grandTotalRemittances", grandTotalRemittances);
        model.addAttribute("date", date);
        System.out.println(model);
        return "report/summaryOfDailyRemittance";
        */
    }

    public Map<String, Object> calculateTotalSummaryOfDailyStatemen(String totalAmount, String totalRemittance){
        Map<String, Object> totalData = new HashMap<>();
        totalData.put("sl", "");
        totalData.put("exchangeCode", "");
        totalData.put("nrtaCode", CommonService.generateClassForText("Grand Total", "fw-bold"));
        totalData.put("exchangeName", "");
        totalData.put("totalAmount", CommonService.generateClassForText(totalAmount, "fw-bold"));
        totalData.put("totalRemittance", CommonService.generateClassForText(totalRemittance, "fw-bold"));
        return totalData;
    }

    @RequestMapping(value="/showDetailsOfDailyStatement", method= RequestMethod.GET)
    public String showDetailsOfDailyStatement(Model model, @RequestParam(defaultValue = "") String startDate, @RequestParam(defaultValue = "") String endDate) {
        if(startDate.isEmpty()){
            startDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        if(endDate.isEmpty()){
            endDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "/report/detailsOfDailyRemittance";
    }
    @RequestMapping(value="/detailsOfDailyStatement", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateDetailsOfDailyStatement(@RequestParam(defaultValue = "") String fromDate, @RequestParam(defaultValue = "") String toDate) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (fromDate.isEmpty()) {
                fromDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            if (toDate.isEmpty()) {
                toDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            resp = reportService.generateDetailsOfDailyStatement(fromDate, toDate);
            if((Integer) resp.get("err") == 1){
                resp = CommonService.getResp(1,"No data found for the selected dates.", null);
            }
        } catch (Exception e) {
            resp = CommonService.getResp(1,"An error occurred while fetching reimbursement data: " + e.getMessage(), null);
        }
        return ResponseEntity.ok(resp);
    }
    @RequestMapping(value="/downloadSummaryOfDailyStatementInPdfFormat", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDailyStatementInPdfFormat(@RequestParam(defaultValue = "") String date) throws Exception {
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        List<ExchangeReportDTO> data = reportService.generateSummaryOfDailyStatement(date);
        if(data.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        byte[] pdfReport = reportService.generateDailyStatementInPdfFormat(data, date);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = commonService.generateFileName("summary_report_", date, ".pdf");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"" );
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }
    @RequestMapping(value = "/downloadSearchFile", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDetailsReportInPdf(@RequestParam("type") String format, @RequestParam(name="fromDate", defaultValue = "") String fromDate, @RequestParam(name="toDate", defaultValue = "") String toDate){
        if(fromDate.isEmpty()){
            fromDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        if(toDate.isEmpty()){
            toDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        try {
            List<ExchangeReportDTO> dataList = reportService.generateDetailsOfDailyRemittances(fromDate, toDate);
            byte[] reportBytes = reportService.generateJasperSearchFileInPdfAndTxtFormat(dataList, format, toDate);
            String fileName = commonService.generateFileName("search_of_", toDate, "." + format.toLowerCase());
            MediaType mediaType = format.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(mediaType)
                    .body(reportBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping(value="/downloaDailyVoucherInPdfFormat", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloaDailyVoucherInPdfFormat(@RequestParam(defaultValue = "") String date) throws Exception {
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        List<ExchangeReportDTO> data = reportService.generateSummaryOfDailyStatement(date);
        for(int i=0; i<data.size();i++){
            data.get(i).setTotalAmountInWords(NumberToWords.convertDoubleToWords(data.get(i).getSumOfAmount()));
        }
        String fileName = commonService.generateFileName("daily_voucher_", date, ".pdf");
        byte[] pdfReport = reportService.generateDailyVoucherInPdfFormat(data, date);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }

    @GetMapping("/generateReport")
    public ResponseEntity<?> viewPdf() throws IOException {
        String date = CommonService.getCurrentDate("yyyy-MM-dd");
        Path filePath = commonService.getReportFile(commonService.generateFileName("summary_report_", date, ".pdf"));
        String fileName = filePath.toString();
        try {
            // Construct the full file path
            File file = new File(fileName);
            // Check if the file exists
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("PDF file not found.");
            }

            // Open the file as a resource
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fileInputStream);

            // Prepare response with PDF content
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File not found.");
        }
    }

    @GetMapping(value="/processReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReport(@AuthenticationPrincipal MyUserDetails userDetails){
        Map<String, Object> resp = new HashMap<>();
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        resp = reportService.processReport(currentDate);
        return ResponseEntity.ok(resp);
    }
    @GetMapping("/showIcashEntryForm")
    public String showIcashEntryForm(Model model){
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("mo", new MoModel());
        return "pages/admin/adminIcashUpload";
    }

    @RequestMapping(value="/generateMo", method= RequestMethod.POST)
    public String generateMo(MoModel mo, Model model, @RequestParam(defaultValue = "") String date) {
        System.out.println("DATE FROM /GENERATE mo url "+date);
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        mo.setMoDate(LocalDate.parse(date));
        MoModel moModel = moModelService.findIfAlreadyGenerated(mo);
        if (moModel == null) {
            moModel = moModelService.processAndGenerateMoData(mo);
        }
        model.addAttribute("moModel", moModel);
        model.addAttribute("date", date);
        return "report/mo";
    }
    @PostMapping("/downloadMoInPdfFormat")
    public ResponseEntity<byte[]> downloadMoInPdfFormat(@RequestParam(defaultValue = "") String date, @ModelAttribute MoModel moModel) throws Exception {
        System.out.println("DATE from downloadMoInPdfFormat ----- "+date);
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        MoModel moModelForPdf = moModelService.generateMoDTOForPreparingPdfFile(moModel, date);
        if(moModelForPdf == null){
            return ResponseEntity.noContent().build();
        }
        byte[] pdfReport = moModelService.generateMoInPdfFormat(moModelForPdf, date);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = commonService.generateFileName("MO_", date, ".pdf");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"" );
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }
    @RequestMapping(value="/showDailyReimbursement", method= RequestMethod.GET)
    public String showDailyReimbursement(Model model, @RequestParam(defaultValue = "") String startDate, @RequestParam(defaultValue = "") String endDate) {
        if(startDate.isEmpty()){
            startDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        if(endDate.isEmpty()){
            endDate = CommonService.getCurrentDate("yyyy-MM-dd");
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "/report/reimbursement";
    }
    @RequestMapping(value = "/getReimbursementData", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReimbursementData(@RequestParam(defaultValue = "") String fromDate, @RequestParam(defaultValue = "") String toDate) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (fromDate.isEmpty()) {
                fromDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            if (toDate.isEmpty()) {
                toDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            resp = reimbursementModelService.insertReimbursementData(LocalDate.parse(fromDate), LocalDate.parse(toDate));
            Object dataObject = resp.get("data");
            if (dataObject instanceof ArrayList<?>) {
                List<?> dataList = (ArrayList<?>) dataObject;
                String valueOfType;
                for (Object obj : dataList) {
                    if (obj instanceof ReimbursementModel) {
                        ReimbursementModel reimbursementData = (ReimbursementModel) obj;
                        valueOfType = reimbursementData.getType();
                        if (valueOfType.equals("4")) {
                            reimbursementData.setType("COC");
                        } else if (valueOfType.equals("2")) {
                            reimbursementData.setType("A/C Payee");
                        }
                    }
                }
            }
            if((Integer) resp.get("err") == 1){
                resp = CommonService.getResp(1,"No data found for the selected dates.", null);
            }
        } catch (Exception e) {
            resp = CommonService.getResp(1,"An error occurred while fetching reimbursement data: " + e.getMessage(), null);
        }
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/downloadDailyReimbursement", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDailyReimbursement(@RequestParam(name="fromDate", defaultValue = "") String fromDate, @RequestParam(name="toDate", defaultValue = "") String toDate, @ModelAttribute MoModel moModel){
        try {
            if (fromDate.isEmpty()) {
                fromDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            if (toDate.isEmpty()) {
                toDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            byte[] contentStream  = reimbursementModelService.loadAllReimbursementByDate(LocalDate.parse(fromDate), LocalDate.parse(toDate));
            String fileName = CommonService.generateDynamicFileName("Reimbursement_", ".csv");
            MediaType mediaType = MediaType.TEXT_PLAIN;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(mediaType)
                    .body(contentStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    @RequestMapping(value = "/downloadDailyReimbursementForIcash", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDailyReimbursementForIcash(@RequestParam(name="fromDate", defaultValue = "") String fromDate, @RequestParam(name="toDate", defaultValue = "") String toDate, @ModelAttribute MoModel moModel){
        try {
            if (fromDate.isEmpty()) {
                fromDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            if (toDate.isEmpty()) {
                toDate = CommonService.getCurrentDate("yyyy-MM-dd");
            }
            byte[] contentStream  = reimbursementModelService.loadAllReimbursementForIcashByDate(LocalDate.parse(fromDate), LocalDate.parse(toDate));
            String fileName = CommonService.generateDynamicFileName("Reimbursement_ICash_", ".csv");
            MediaType mediaType = MediaType.TEXT_PLAIN;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(mediaType)
                    .body(contentStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getReportFile")
    public ResponseEntity<Resource> getReportFile(@RequestParam String fileName){
        try{
            Path filePath = commonService.generateOutputFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build(); // Handle any IO exceptions
        }
    }

    //for getting live data exchange wise
    @GetMapping(value="/getExchangeData", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExchangeWiseData(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam(defaultValue = "") String date){
        Map<String, Object> resp = new HashMap<>();
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        int userId = (int) userData.get("userid");
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        resp = reportService.getExchangeWiseData(date, userId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/updateTotalAmountBulk", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFileInfoModelTotalAmountBulk(){
        Map<String, Object> resp = new HashMap<>();
        List<FileInfoModel> fileInfoModel = fileInfoModelService.getUploadedFileNotHavingTotalAmount();
        if(fileInfoModel.isEmpty())     return ResponseEntity.ok(CommonService.getResp(1, "No Data Found", null));
        int totalCount = 0;
        for(FileInfoModel fModel: fileInfoModel){
            Double total = 0.0;
            int onlineCount = CommonService.convertStringToInt(fModel.getOnlineCount());
            int beftnCount = CommonService.convertStringToInt(fModel.getBeftnCount());
            int accPayeeCount = CommonService.convertStringToInt(fModel.getAccountPayeeCount());
            int cocCount = CommonService.convertStringToInt(fModel.getCocCount());
            int fileInfoModelId = fModel.getId();
            if(onlineCount > 0){
                Map<String, Object> onlineMap =  customQueryService.calculateTotalAmountForConvertedModel(1,fileInfoModelId);
                if((Integer) onlineMap.get("err") == 1) continue;
                for(Map<String, Object> onlineData: (List<Map<String, Object>>) onlineMap.get("data")){
                    total += CommonService.convertStringToDouble(onlineData.get("totalAmount").toString());
                }
            }
            if(accPayeeCount > 0){
                Map<String, Object> accountPayeeMap =  customQueryService.calculateTotalAmountForConvertedModel(2,fileInfoModelId);
                if((Integer) accountPayeeMap.get("err") == 1) continue;
                for(Map<String, Object> accountPayeeData: (List<Map<String, Object>>) accountPayeeMap.get("data")){
                    total += CommonService.convertStringToDouble(accountPayeeData.get("totalAmount").toString());
                }
            }
            
            if(beftnCount > 0){
                Map<String, Object> beftnMap =  customQueryService.calculateTotalAmountForConvertedModel(3,fileInfoModelId);
                if((Integer) beftnMap.get("err") == 1) continue;
                for(Map<String, Object> beftnData: (List<Map<String, Object>>) beftnMap.get("data")){
                    total += CommonService.convertStringToDouble(beftnData.get("totalAmount").toString());
                }
            }
            
            if(cocCount > 0 ){
                if(fModel.getIsSettlement() == 0){
                    Map<String, Object> cocMap =  customQueryService.calculateTotalAmountForConvertedModel(4,fileInfoModelId);
                    if((Integer) cocMap.get("err") == 1) continue;
                    for(Map<String, Object> cocData: (List<Map<String, Object>>) cocMap.get("data")){
                        total += CommonService.convertStringToDouble(cocData.get("totalAmount").toString());
                    }
                }
                if(fModel.getIsSettlement() == 1){
                    Map<String, Object> cocMap =  customQueryService.calculateTotalAmountForConvertedModel(5,fileInfoModelId);
                    if((Integer) cocMap.get("err") == 1) continue;
                    for(Map<String, Object> cocData: (List<Map<String, Object>>) cocMap.get("data")){
                        total += CommonService.convertStringToDouble(cocData.get("totalAmount").toString());
                    }
                }
                
            }
            String totalStr = CommonService.convertNumberFormat(total, 2);
            fileInfoModelService.updateTotalAmountById(fileInfoModelId, totalStr);
            totalCount++;
        }
        if(totalCount == 0)    CommonService.getResp(1, "No Data Found", null);
        else resp = CommonService.getResp(0, "Data Updated", null);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam(defaultValue = "") String type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return HttpStatus.UNAUTHORIZED.getReasonPhrase();
        int userId = (int) userData.get("userid");
        Map<String, Object> searchType = CommonService.getSerachType(type);
        Map<String, String> exchangeMap = new HashMap<>();
        if(userData.containsKey("exchangeMap")) exchangeMap = (Map<String, String>) userData.get("exchangeMap");
        model.addAttribute("exchangeMap", exchangeMap);
        String sidebar = CommonService.getSidebarNameByUserid(userId);
        model.addAttribute("sidebar", sidebar);
        model.addAttribute("searchType", searchType);
        model.addAttribute("type", type);
        if(type.equals("2") && userId != 0){
            model.addAttribute("errorMessage", "Invalid Attempt. You are not allowed to perform this operation");
            return "fragments/error";
        }
        return "pages/user/search";
    }

    @GetMapping(value="/getSearch", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSearch(@RequestParam("searchType") String searchType, @RequestParam("searchValue") String searchValue, 
        @RequestParam(defaultValue = "") String type){
        Map<String, Object> resp = new HashMap<>();
        if(("2").equals(type)){
            resp = reportService.getCorrectionSearch(searchType, searchValue);
        }else resp = reportService.getSearch(searchType, searchValue);;
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping(value="/file/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteByFileInfoModelById(@PathVariable int id, HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        int userId = (int) userData.get("userid");
        if(userId != 0) return ResponseEntity.ok(CommonService.getResp(1, "You are not allowed to perform this operation", null));
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        if(role.get("isAdmin") == 1){
            userId = (int) userData.get("adminUserId");
        }
        Map<String, Object> resp = reportService.deleteByFileInfoModelById(id, userId, request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/editForm/{id}")
    public String editForm(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @PathVariable("id") String id, @RequestParam("type") String type){
        return "pages/admin/editForm";
    }

    @GetMapping(value = "/getEditData/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEditData(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @PathVariable("id") String id, @RequestParam("type") String type){
        Map<String, Object> resp = reportService.getEditData(CommonService.convertStringToInt(id), type, 0);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value ="/update", produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateIndividualDataById(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam Map<String, String> formData, Model model, 
        HttpServletRequest request){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        int userId = (int) userData.get("userid");
        if(userId != 0) return ResponseEntity.ok(CommonService.getResp(1, "You are not allowed to perform this operation", null));
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        if(role.get("isAdmin") == 1){
            userId = (int) userData.get("adminUserId");
        }
        formData.remove("_csrf");
        formData.remove("_csrf_header");
        try{
            resp = reportService.updateIndividualDataById(formData, userId, request);
        }catch(Exception e){
            return ResponseEntity.ok(CommonService.getResp(1, e.getMessage(), null));
        }
        
        return ResponseEntity.ok(resp);
    }
    @GetMapping(value="/getRouting", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoutingDetails(@RequestParam(defaultValue = "") String routingNo, @RequestParam(defaultValue = "") String bankCode){
        Map<String, Object> resp = new HashMap<>();
        if(routingNo.isEmpty() && bankCode.isEmpty())   return ResponseEntity.ok(CommonService.getResp(1, "Please select routing No or Bank code", null));
        resp = customQueryService.getRoutingDetails(routingNo, bankCode);
        return ResponseEntity.ok(resp);
    }

}
