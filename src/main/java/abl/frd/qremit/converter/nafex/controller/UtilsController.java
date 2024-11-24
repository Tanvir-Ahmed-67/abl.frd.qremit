package abl.frd.qremit.converter.nafex.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.FileInfoModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;

@Controller
@RequestMapping("/utils")
public class UtilsController {
    private final MyUserDetailsService myUserDetailsService;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    private String fileUploadPage = "fragments/file_upload_form";
    public UtilsController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    
    @GetMapping("/index")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam String id){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        int showDropDown = this.showDropDown(id);
        if(showDropDown == 1){
            Map<String, Object> dropdown = getDropdown(id);
            model.addAttribute("dropdown", dropdown);
        }
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(id);
        model.addAttribute("showDropDown", showDropDown);
        model.addAttribute("exchangeHouseModel", exchangeHouseModel);
        return fileUploadPage;
    }
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails,@RequestParam("file") MultipartFile file,@RequestParam("exchangeCode") String exchangeCode, @RequestParam(defaultValue = "") String fileType , Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        model.addAttribute("file", file);
        model.addAttribute("exchangeCode", exchangeCode);
        
        int showDropDown = this.showDropDown(exchangeCode);
        if(showDropDown == 1){
            switch (fileType) {
                case "BEFTN":
                case "API":
                    break;
                default:
                    fileType = "";
                    break;
            }
            if(fileType.equals("")){
                String message = "Please select file type";
                model.addAttribute("message", message);
                return CommonService.uploadSuccesPage;
            } 
            model.addAttribute("fileType", fileType); 
        }

        String redirectUrl ="";
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        if(exchangeHouseModel != null && exchangeCode.equals(exchangeHouseModel.getExchangeCode())){
            model.addAttribute("nrtaCode", exchangeHouseModel.getNrtaCode());
            redirectUrl = "/" + exchangeHouseModel.getBaseTableName() + "Upload?nrtaCode=" + exchangeHouseModel.getNrtaCode();  //generate dynamic URL from database
        }
        return "forward:" + redirectUrl;
    }

    public int showDropDown(String exCode){
        int showDropDown = 0;
        switch (exCode) {
            case "7010226":
            case "7010299":
            case "7010228":
                showDropDown = 1;
                break;
            default:
                break;
        }
        return showDropDown;
    }

    public Map<String, Object> getDropdown(String exCode){
        Map<String, Object> resp = new HashMap<>();
        String api = "API";
        if(exCode.equals("7010228"))    api = "Account Payee";
        resp.put("API",api);
        resp.put("BEFTN", "BEFTN");
        return resp;
    }

    @GetMapping("/errorReport")
    public String errorReport(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        return "pages/user/errorReport";
    }
    
    @GetMapping("/uploadApi")
    public String uploadApiUi(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        String currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        model.addAttribute("currentDate", currentDate);
        return "pages/admin/adminApiUpload";
    }

    @GetMapping("/getSettlement")
    @ResponseBody
    public Map<String, Object> getSettlement(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam(defaultValue = "") String currentDate){
        Map<String, Object> resp = new HashMap<>();
        if(currentDate == null || currentDate.trim().isEmpty())   currentDate = CommonService.getCurrentDate("yyyy-MM-dd");
        else currentDate = currentDate.trim();
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelService.loadAllIsSettlementExchangeHouse(1);
        List<Map<String, Object>> settlementList = fileInfoModelService.getSettlementList(exchangeHouseModelList, currentDate);
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        String action = "";
        int hasSettlementDailyCount = exchangeHouseModelService.calculateSumOfHasSettlementDaily(exchangeHouseModelList);
        int totalCount = 0;
        for(Map<String, Object> settlement: settlementList){
            Map<String, Object> dataMap = new HashMap<>();
            int count = (int) settlement.get("count");
            if(count >= 1){
                action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-success btn-sm", "","Processed");
                totalCount++;
            }else action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm", "","Not Processed");
            dataMap.put("sl", i++);
            dataMap.put("currentDate", currentDate);
            dataMap.put("exchangeName", settlement.get("exchangeName"));
            dataMap.put("action", action);
            dataList.add(dataMap);
        } 
        resp = CommonService.getResp(0, "", dataList);
        if(totalCount >= hasSettlementDailyCount)  resp.put("generateBtn", "1");
        return resp;
    }

    
}
