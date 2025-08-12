package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.service.BeftnModelService;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.FileDownloadService;
import abl.frd.qremit.converter.service.FileInfoModelService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@SuppressWarnings("unchecked")
public class BeftnModelController {
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    BeftnModelService beftnModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    public BeftnModelController(){
    }

    @GetMapping("/downloadbeftnMain")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadMainFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = beftnModelService.loadAndUpdateUnprocessedBeftnMainData(0);
        int countRemaining = beftnModelService.countRemainingBeftnDataMain();
        String fileName = CommonService.generateDynamicFileName("Beftn_Main_", ".xlsx");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("3", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/downloadBeftnIncentive")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadIncentiveFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = beftnModelService.loadAndUpdateUnprocessedBeftnIncentiveData(0);
        int countRemaining = beftnModelService.countRemainingBeftnDataIncentive();
        String fileName = CommonService.generateDynamicFileName("Beftn_Incentive_", ".xlsx");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("3", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/notProcessingBeftnIncentive", produces = "application/json")
    public ResponseEntity<Map<String, Object>> calculateNotProcessingBeftnIncentive(){
        Map<String, Object> resp = beftnModelService.calculateNotProcessingBeftnIncentive();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/uploadBeftnReturn")
    public String uploadBeftnReturnUi(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails)); 
        if(authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/login";
        }
        return "pages/user/beftn_return_upload";
    }

    @PostMapping(value="/processBeftnReturnUpload", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadBeftnReturn(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, @ModelAttribute("exchangeCode") String exchangeCode, Model model){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        if((Integer) role.get("isUser") != 1)  return ResponseEntity.ok(CommonService.getResp(0, "You are not allowed to perform this operation", null));
        if(userData.containsKey("exchangeMap"))     model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        int userId = (int) userData.get("userid");
        if (CommonService.hasCSVFormat(file)){
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                resp = beftnModelService.uploadBeftnReturn(file,userId, exchangeCode);
            }else resp = CommonService.getResp(1, "File With The Name "+ file.getOriginalFilename() +" Already Exists !!", null);
        }else resp = CommonService.getResp(1, "Please Upload a CSV File!", null);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/getBeftnReturnReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBeftnReturnReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam Map<String, String> formData){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        int userId = (int) userData.get("userid");
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        resp = beftnModelService.getExchangeWiseBeftnReturnReport(formData, userId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/viewBeftnReturnFile", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewBeftnReturnFile(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam String id){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        resp = beftnModelService.getBeftnReturnReportByFileInfoModelId(CommonService.convertStringToInt(id));
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value="/getBeftnReturnUploadedFile", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBeftnReturnUploadedFile(@AuthenticationPrincipal MyUserDetails userDetails,Model model,@RequestParam(defaultValue = "") String date,
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
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        String baseUrl = ((Integer) role.get("isUser") != 1) ? "/adminReport": "/user-home-page";
        if(userData.containsKey("exchangeMap")) model.addAttribute("exchangeMap", userData.get("exchangeMap"));
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<FileInfoModel> fileInfoModelList = fileInfoModelService.getUploadedFileDetailsByExchangeCode("666666", date);
        if(fileInfoModelList.isEmpty()) return ResponseEntity.ok(CommonService.getResp(1, "No data found", dataList));
        int i = 1;
        for(FileInfoModel fileInfoModel: fileInfoModelList){
            String action = CommonService.generateTemplateBtn("template-editBtn.txt",baseUrl + "?type=17&id=" + fileInfoModel.getId(),"btn-info btn-sm text-white","","View");
            Map<String, Object> data = new HashMap<>();
            data.put("sl", i++);
            data.put("exchangeCode", fileInfoModel.getExchangeCode());
            data.put("fileName", fileInfoModel.getFileName());
            data.put("totalCount", fileInfoModel.getTotalCount());
            data.put("uploadDateTime", fileInfoModel.getUploadDateTime());
            data.put("action", action);
            dataList.add(data);
        }
        resp.put("data", dataList);
        return ResponseEntity.ok(resp);
    }

}
