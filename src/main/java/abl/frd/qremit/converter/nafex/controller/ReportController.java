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
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;
import abl.frd.qremit.converter.nafex.repository.CustomQueryRepository;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.CustomQueryService;
import abl.frd.qremit.converter.nafex.service.ErrorDataModelService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.FileInfoModelService;
import abl.frd.qremit.converter.nafex.service.LogModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.ReportService;

import javax.servlet.http.HttpServletResponse;

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
    
    @GetMapping("/report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUploadedFileInfo(@AuthenticationPrincipal MyUserDetails userDetails,Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();

        String[] columnData = {"sl", "exchangeCode", "uploadDateTime", "fileName", "cocCount", "beftnCount", "onlineCount", "accountPayeeCount", "totalCount","action"};
        String[] columnTitles = {"SL", "Exchange Code", "Upload Date", "File Name", "COC", "BEFTN", "Online", "Account Payee", "Total","Action"};
        List<Map<String, String>> columns = commonService.createColumns(columnData, columnTitles);
        resp.put("columns", columns);
        
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
        String[] columnTitles = {"SL", "Bank Name", "Branch Name", "Beneficiary Name", "Account No", "Transaction No", "Amount", "Exchange Code", "Remitter Name","Type"};
        List<Map<String, String>> columns = commonService.createColumns(columnData, columnTitles);
        resp.put("columns", columns);

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
            
            //check flag status = 1
            Map<String, String> type = new HashMap<>();
            type.put("check_beftn",String.valueOf(fdata.get("check_beftn")));
            type.put("check_coc",String.valueOf(fdata.get("check_coc")));
            type.put("check_account_payee",String.valueOf(fdata.get("check_account_payee")));
            type.put("check_t24",String.valueOf(fdata.get("check_t24")));
            dataMap.put("remType", getRemittanceType(type));

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
    public ResponseEntity<Map<String, Object>> getErrorReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();

        String[] columnData = {"sl", "bankName", "routingNo", "branchName", "beneficiaryName", "beneficiaryAccountNo", "transactionNo", "amount", "exchangeCode", "errorMessage","action"};
        String[] columnTitles = {"SL", "Bank Name", "Routing No", "Branch Name", "Beneficiary Name", "Account No", "Transaction No", "Amount", "Exchange Code", "Error Mesage","Action"};
        List<Map<String, String>> columns = commonService.createColumns(columnData, columnTitles);
        resp.put("columns", columns);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();

            List<Map<String, Object>> dataList = new ArrayList<>();
            int sl = 1;
            String action = "";
            String btn = "";
            List<ErrorDataModel> errorDataModel = errorDataModelService.findUserModelListByIdAndUpdateStatus(userId, 0);
            for(ErrorDataModel emodel: errorDataModel){
                Map<String, Object> dataMap = new HashMap<>();
                btn = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm edit_error",String.valueOf(emodel.getId()),"Edit");
                btn += CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm delete_error",String.valueOf(emodel.getId()),"Delete");
                action = CommonService.generateTemplateBtn("template-btngroup.txt", "#", "", "", btn);
                dataMap.put("sl", sl++);
                dataMap.put("bankName", emodel.getBankName());
                dataMap.put("branchName", emodel.getBranchName());
                dataMap.put("routingNo", emodel.getBranchCode());
                dataMap.put("beneficiaryName", emodel.getBeneficiaryName());
                dataMap.put("beneficiaryAccountNo", emodel.getBeneficiaryAccount());
                dataMap.put("transactionNo", emodel.getTransactionNo());
                dataMap.put("amount", emodel.getAmount());
                dataMap.put("exchangeCode", emodel.getExchangeCode());
                dataMap.put("errorMessage", emodel.getErrorMessage());
                dataMap.put("action", action);

                dataList.add(dataMap);
            }
            resp.put("data", dataList);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/getErrorUpdateReport")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getErrorUpdateReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model){
        //model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();

        String[] columnData = {"sl", "bankName", "routingNo", "branchName", "beneficiaryName", "beneficiaryAccountNo", "transactionNo", "amount", "exchangeCode", "errorMessage","action"};
        String[] columnTitles = {"SL", "Bank Name", "Routing No", "Branch Name", "Beneficiary Name", "Account No", "Transaction No", "Amount", "Exchange Code", "Error Mesage","Action"};
        List<Map<String, String>> columns = commonService.createColumns(columnData, columnTitles);
        resp.put("columns", columns);

        List<ErrorDataModel> errorDataModel = errorDataModelService.findUserModelListByUpdateStatus(1);
        List<Map<String, Object>> dataList = new ArrayList<>();
        int sl = 1;
        String action = "";
        for(ErrorDataModel emodel: errorDataModel){
            String errorDataId = String.valueOf(emodel.getId());
            List<Map<String, Object>> logData =  logModelService.findLogModelByErrorDataId(errorDataId);
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
            action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-info btn-sm round approve_error", errorDataId,"Approve");
            
            dataMap.put("sl", sl++);
            dataMap.put("bankName", updatedDataMap.get("bankName"));
            dataMap.put("branchName", updatedDataMap.get("branchName"));
            dataMap.put("routingNo", updatedDataMap.get("branchCode"));
            dataMap.put("beneficiaryName", updatedDataMap.get("beneficiaryName"));
            dataMap.put("beneficiaryAccountNo", updatedDataMap.get("beneficiaryAccount"));
            dataMap.put("transactionNo", updatedDataMap.get("transactionNo"));
            dataMap.put("amount", updatedDataMap.get("amount"));
            dataMap.put("exchangeCode", emodel.getExchangeCode());
            dataMap.put("errorMessage", emodel.getErrorMessage());
            dataMap.put("action", action);

            dataList.add(dataMap);
        }
        resp.put("data", dataList);
        return ResponseEntity.ok(resp);
    }

    public String getRemittanceType(Map<String, String> type){
        Map<String, String> remType = new HashMap<>();
        remType.put("check_beftn","BEFTN");
        remType.put("check_coc","COC");
        remType.put("check_account_payee","A/C Payee");
        remType.put("check_t24","Online");

        for(Map.Entry<String, String> entry: type.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            if ("1".equals(value)) {
                return remType.get(key);
            }
        }
        return "";
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
