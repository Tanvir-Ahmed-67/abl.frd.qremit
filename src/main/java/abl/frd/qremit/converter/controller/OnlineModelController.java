package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.FileDownloadService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
import abl.frd.qremit.converter.service.OnlineModelService;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class OnlineModelController {
    private final OnlineModelService onlineModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    public OnlineModelController(OnlineModelService onlineModelService){
        this.onlineModelService = onlineModelService;
    }

    @GetMapping(value="/downloadonline", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = onlineModelService.loadAndUpdateUnprocessedOnlineData(0);
        int countRemaining = onlineModelService.countRemainingOnlineData();
        String fileName = CommonService.generateDynamicFileName("Online_", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("1", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/countOnlineAfterDownloadButtonClicked")
    @ResponseBody
    public int countOnlineAfterDownloadButtonClicked(){
        int count = onlineModelService.countUnProcessedOnlineData(0);
        return count;
    }

    @PostMapping("/converted_data_onlineUpload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, @ModelAttribute("fileType") String fileType, 
         @ModelAttribute("exchangeCode") String exchangeCode, @RequestParam("nrtaCode") String nrtaCode, @RequestParam("tbl") String tbl, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/login";
        }
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return "redirect:/login?error=unauthorized";
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        int userId = (int) userData.get("userid");
        String message = "";
        if (CommonService.hasCSVFormat(file)) {
            if(!commonService.ifFileExist(file.getOriginalFilename())) {
                try{
                    Map<String, Object> resp = onlineModelService.uploadQremitIncentive(file, userId, exchangeCode);
                    if( (int) resp.get("err") == 0){
                        model.addAttribute("fileInfo", resp.get("fileInfo"));
                    }else{
                        model.addAttribute("message", resp.get("msg").toString());
                    }
                    return "pages/user/qremit_incentive";
                }catch(Exception e){
                    message = "Could Not Upload The File: " + file.getOriginalFilename() +"";
                    model.addAttribute("message", message);
                    return "pages/user/qremit_incentive";
                }
            }
            model.addAttribute("message", "File With The Name "+ file.getOriginalFilename() +" Already Exists !!");
            return "pages/user/qremit_incentive";
        }
        return "";

    }

}
