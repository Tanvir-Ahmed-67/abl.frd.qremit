package abl.frd.qremit.converter.nafex.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.FileInfoModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.ReportService;

@RestController
public class ReportController {

    private final MyUserDetailsService myUserDetailsService;
    private final FileInfoModelService fileInfoModelService;
    private final ReportService reportService;
    @Autowired
    CommonService commonService;

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
            for (FileInfoModel fModel : fileInfoModel) {
                Map<String, Object> dataMap = new HashMap<>();
                String action = "<button type='button' class='btn btn-info round view_exchange' id='" + fModel.getId() + "'>View</button>";
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
            
            /* 
            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("exchangeCode","Total");
            totalMap.put("totalCount",totalCount);
            dataList.add(totalMap);
            */
            //System.out.println(totalCount);
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

        ExchangeHouseModel exchangeHouseModel = reportService.findByExchangeCode(exchangeCode);
        String baseTableName = exchangeHouseModel.getBaseTableName();
        String tbl = "base_data_table_" + baseTableName;
        
        Map<String,Object> fileInfo = reportService.getFileDetails(tbl,id);
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

    
}
