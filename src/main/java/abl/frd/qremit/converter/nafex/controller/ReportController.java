package abl.frd.qremit.converter.nafex.controller;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import abl.frd.qremit.converter.nafex.helper.NumberToWords;
import abl.frd.qremit.converter.nafex.model.*;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.CustomQueryService;
import abl.frd.qremit.converter.nafex.service.ErrorDataModelService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.FileInfoModelService;
import abl.frd.qremit.converter.nafex.service.LogModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.ReportService;

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
    private static final String PDF_DIRECTORY = "D:/Report/";
    

    public ReportController(MyUserDetailsService myUserDetailsService,FileInfoModelService fileInfoModelService,ReportService reportService){
        this.myUserDetailsService = myUserDetailsService;
        this.fileInfoModelService = fileInfoModelService;
        this.reportService = reportService;
    }

    @GetMapping("/getReportColumn")
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
            default:
                columnData = new String[] {"sl", "exchangeCode", "uploadDateTime", "fileName", "cocCount", "beftnCount", "onlineCount", "accountPayeeCount", "totalCount","action"};
                columnTitles = new String[] {"SL", "Exchange Code", "Upload Date", "File Name", "COC", "BEFTN", "Online", "Account Payee", "Total","Action"};
                break;
            case "2":
                columnData = new String[] {"sl", "bankName", "branchName", "beneficiaryName", "beneficiaryAccountNo", "transactionNo", "amount", "exchangeCode", "remitterName","remType"};
                columnTitles = new String[] {"SL", "Bank Name", "Branch Name", "Beneficiary Name", "Account No", "Transaction No", "Amount", "Exchange Code", "Remitter Name","Type"};
                break;
            case "3":
            case "4":
                columnData = new String[] {"sl", "bankName", "routingNo", "branchName", "beneficiaryName", "beneficiaryAccountNo", "transactionNo", "amount", "exchangeCode", "errorMessage","action"};
                columnTitles = new String[] {"SL", "Bank Name", "Routing No", "Branch Name", "Beneficiary Name", "Account No", "Transaction No", "Amount", "Exchange Code", "Error Mesage","Action"};
                break;
        }
        return CommonService.createColumns(columnData, columnTitles);
    }
    
    @GetMapping("/report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUploadedFileInfo(@AuthenticationPrincipal MyUserDetails userDetails,Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
            List<FileInfoModel> fileInfoModel = fileInfoModelService.getUploadedFileDetails(userId);
            
            List<Map<String, Object>> dataList = new ArrayList<>();
            int sl = 1;
            int totalCount = 0;
            String action = "";
            for (FileInfoModel fModel : fileInfoModel) {
                Map<String, Object> dataMap = new HashMap<>();
                action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round view_exchange", String.valueOf(fModel.getId()),"View");
                action += "<input type='hidden' id='exCode_" + fModel.getId() + "' value='" + fModel.getExchangeCode() + "' />";
                dataMap.put("sl", sl++);
                dataMap.put("id", fModel.getId());
                dataMap.put("exchangeCode", fModel.getExchangeCode());
                dataMap.put("uploadDateTime", fModel.getUploadDateTime());
                dataMap.put("fileName", fModel.getFileName());
                dataMap.put("cocCount", fModel.getCocCount());
                dataMap.put("beftnCount", fModel.getBeftnCount());
                dataMap.put("onlineCount", fModel.getOnlineCount());
                dataMap.put("accountPayeeCount", fModel.getAccountPayeeCount());
                totalCount += Integer.valueOf(fModel.getTotalCount());
                dataMap.put("totalCount", fModel.getTotalCount());
                dataMap.put("action", action); // Example action, customize as needed
                dataList.add(dataMap);
            }
            resp.put("data", dataList);
            return ResponseEntity.ok(resp);
        }
        else{
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/fileReport")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFileDetails(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam String id,@RequestParam String exchangeCode){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        
        String[] columnData = {"sl", "bankName", "branchName", "beneficiaryName", "beneficiaryAccountNo", "transactionNo", "amount", "exchangeCode", "remitterName","remType"};
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
        }else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        String tbl = CommonService.getBaseTableName(exchangeHouseModel.getBaseTableName());
        
        Map<String,Object> fileInfo = customQueryService.getFileDetails(tbl,id);
        if((Integer) fileInfo.get("err") == 1)  return ResponseEntity.ok(fileInfo);

        List<Map<String, Object>> dataList = new ArrayList<>();
        int sl = 1;
        double totalAmount = 0;

        for(Map<String,Object> fdata: (List<Map<String, Object>>) fileInfo.get("data")){
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("sl", sl++);
            dataMap.put("bankName",fdata.get("bank_name"));
            dataMap.put("branchName",fdata.get("branch_name"));
            dataMap.put("beneficiaryAccountNo",fdata.get("beneficiary_account_no"));
            dataMap.put("beneficiaryName",fdata.get("beneficiary_name"));
            dataMap.put("remitterName",fdata.get("remitter_name"));
            dataMap.put("transactionNo",fdata.get("transaction_no"));
            dataMap.put("exchangeCode",fdata.get("exchange_code"));
            dataMap.put("amount",fdata.get("amount"));
            totalAmount += Double.parseDouble(String.valueOf(fdata.get("amount")));
            Map<String, Object> types = CommonService.getRemittanceTypes();
            dataMap.put("remType", types.get(fdata.get("type_flag")));
            dataList.add(dataMap);
        }
        //System.out.println(totalAmount);
        Map<String, Object> totalAmountMap = commonService.getTotalAmountData(columnData, totalAmount,"beneficiaryName");
        dataList.add(totalAmountMap);

        resp.put("err", fileInfo.get("err"));
        resp.put("msg", fileInfo.get("msg"));
        resp.put("data",dataList);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/errorReport")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getErrorReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model, 
        @RequestParam(defaultValue = "") String id){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        int fileInfoModelId = 0;
        if(!id.isEmpty())  fileInfoModelId = Integer.parseInt(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
            List<Map<String, Object>> dataList = errorDataModelService.getErrorReport(userId, fileInfoModelId);
            resp.put("data", dataList);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/getErrorUpdateReport")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getErrorUpdateReport(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        //model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = errorDataModelService.getErrorUpdateReport();
        resp.put("data", dataList);
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value="/summaryOfDailyStatement", method= RequestMethod.GET)
    public String generateSummaryOfDailyStatement(Model model) {
        List<ExchangeReportDTO> exchangeReport = reportService.generateSummaryOfDailyStatement();
        Double grandTotalAmount = 0.00;
        String commaFormattedGrandTotalAmount="";
        int grandTotalRemittances=0;
        for(ExchangeReportDTO exchangeReportDTO: exchangeReport){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
            grandTotalAmount = grandTotalAmount+exchangeReportDTO.getTotalAmountCount();
            grandTotalRemittances = grandTotalRemittances+exchangeReportDTO.getTotalRowCount();
            commaFormattedGrandTotalAmount = exchangeReportDTO.formattedAmount.format(grandTotalAmount);
        }
        model.addAttribute("summaryReportContent", exchangeReport);
        model.addAttribute("grandTotalAmount", commaFormattedGrandTotalAmount);
        model.addAttribute("grandTotalRemittances", grandTotalRemittances);
        return "/report/summaryOfDailyRemittance";
    }

    @RequestMapping(value="/detailsOfDailyStatement", method= RequestMethod.GET)
    public String generateDetailsOfDailyStatement(@RequestParam(defaultValue = "html") String format, Model model) throws FileNotFoundException {
        List<ExchangeReportDTO> exchangeReport = reportService.generateDetailsOfDailyStatement();
        for(ExchangeReportDTO exchangeReportDTO: exchangeReport){
            exchangeReportDTO.setExchangeName(exchangeHouseModelService.findByExchangeCode(exchangeReportDTO.getExchangeCode()).getExchangeName());
        }
        model.addAttribute("detailsReportContent", exchangeReport);
        return "/report/detailsOfDailyRemittance";
    }

    @RequestMapping(value="/downloadSummaryOfDailyStatementInPdfFormat", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDailyStatementInPdfFormat() throws JRException, FileNotFoundException {
        // Getting the data as List
        List<ExchangeReportDTO> data = reportService.generateSummaryOfDailyStatement();

        // Generating the PDF report and storing here - D:\\Report"+"\\Report.pdf
        byte[] pdfReport = reportService.generateDailyStatementInPdfFormat(data);

        // Set the headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Summary_Report.pdf\"");

        // Return the response with the PDF as a byte array
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }
    @RequestMapping(value = "/downloadDetailsOfDailyStatement", method= RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDetailsReportInPdf(@RequestParam("type") String format){
        try {
            // Prepare parameters and data source as needed
            Map<String, Object> parameters = new HashMap<>();
            List<ExchangeReportDTO> dataList = reportService.generateDetailsOfDailyStatement();

            // Call the method to generate the report
            byte[] reportBytes = reportService.generateDetailsJasperReport(dataList, format);
            // Set the content type and header for attachment download
            String fileName = "Details_Report." + format.toLowerCase();
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
    public ResponseEntity<byte[]> downloaDailyVoucherInPdfFormat() throws JRException, FileNotFoundException {
        NumberToWords numberToWords = new NumberToWords();
        // Getting the data as List
        List<ExchangeReportDTO> data = reportService.generateSummaryOfDailyStatement();
        for(int i=0; i<data.size();i++){
            data.get(i).setTotalAmountInWords(numberToWords.convertDoubleToWords(data.get(i).getSumOfAmount()));
        }

        // Generating the PDF report and storing here - D:\\Report"+"\\DailyVoucher.pdf
        byte[] pdfReport = reportService.generateDailyVoucherInPdfFormat(data);
        // Set the headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"DailyVoucher.pdf\"");

        // Return the response with the PDF as a byte array
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }

    @GetMapping("/generateReport")
    public ResponseEntity<?> viewPdf() {
        String fileName = "Report" + "_" + CommonService.getCurrentDate();
        try {
            // Construct the full file path
            File file = new File(PDF_DIRECTORY + fileName + ".pdf");
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
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while reading the file.");
        }
    }

    @GetMapping("/processReport")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReport(@AuthenticationPrincipal MyUserDetails userDetails){
        Map<String, Object> resp = new HashMap<>();
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        resp = reportService.processReport(currentDate);
        return ResponseEntity.ok(resp);
    }

}
