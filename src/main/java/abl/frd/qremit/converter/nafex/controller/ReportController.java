package abl.frd.qremit.converter.nafex.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.FileInfoModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;

@RestController
public class ReportController {

    private final MyUserDetailsService myUserDetailsService;
    private final FileInfoModelService fileInfoModelService;

    public ReportController(MyUserDetailsService myUserDetailsService,FileInfoModelService fileInfoModelService){
        this.myUserDetailsService = myUserDetailsService;
        this.fileInfoModelService = fileInfoModelService;
    }
    
    @GetMapping("/report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUploadedFileInfo(@AuthenticationPrincipal MyUserDetails userDetails,Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();

        String[] columnData = {"sl", "exchangeCode", "uploadDateTime", "fileName", "cocCount", "beftnCount", "onlineCount", "accountPayeeCount", "totalCount","action"};
        String[] columnTitles = {"SL", "Exchange Code", "Upload Date", "File Name", "COC", "BEFTN", "Online", "Account Payee", "Total","Action"};
        List<Map<String, String>> columns = createColumns(columnData, columnTitles);
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
                String action = "<button type='button' class='btn btn-info round' id='upload_" + fModel.getId() + "'>View</button>";
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

    @GetMapping("/")

    // Helper method to create columns dynamically from arrays
    private List<Map<String, String>> createColumns(String[] columnData, String[] columnTitles) {
        List<Map<String, String>> columns = new ArrayList<>();
        for (int i = 0; i < columnData.length; i++) {
            columns.add(createColumn(columnData[i], columnTitles[i]));
        }
        return columns;
    }
    
    // Helper method to create a column map
    private Map<String, String> createColumn(String data, String title) {
        Map<String, String> column = new HashMap<>();
        column.put("data", data);
        column.put("title", title);
        return column;
    }
    
}
