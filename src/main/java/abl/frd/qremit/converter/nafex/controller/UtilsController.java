package abl.frd.qremit.converter.nafex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.HttpServletRequest;

import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
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
    private String fileUploadPage = "/fragments/file_upload_form";
    public UtilsController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    
    @GetMapping("/index")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam String id){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        int showDropDown = this.showDropDown(id);
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
            if(fileType == ""){
                String message = "Please select file type";
                model.addAttribute("message", message);
                return commonService.uploadSuccesPage;
            } 
            model.addAttribute("fileType", fileType); 
        }

        String redirectUrl ="";
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        if(exchangeHouseModel != null && exchangeCode.equals(exchangeHouseModel.getExchangeCode())){
            redirectUrl = "/" + exchangeHouseModel.getBaseTableName() + "Upload";  //generate dynamic URL from database
        }
        return "forward:" + redirectUrl;
    }

    public int showDropDown(String exCode){
        int showDropDown = 0;
        switch (exCode) {
            case "7010226":
            case "7010299":
                showDropDown = 1;
                break;
            default:
                break;
        }
        return showDropDown;
    }

    @GetMapping("/errorReport")
    public String errorReport(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        return "/pages/user/errorReport";
    }
    
    @GetMapping("/uploadApi")
    public String uploadApiUi(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelService.loadAllIsApiExchangeHouse(1);
        model.addAttribute("exchangeHouseModelList", exchangeHouseModelList);
        //model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        return "/pages/admin/adminApiUpload";
    }

    @PostMapping("/uploadApiData")
    //@ResponseBody
    public String uploadApiData(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam Map<String, String> formData, @RequestParam("file") MultipartFile file, Model model, HttpServletRequest request){
        String exchangeCode = formData.get("exchangeCode");
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        model.addAttribute("fileType", formData.get("fileType"));
        model.addAttribute("exchangeCode", exchangeCode);
        model.addAttribute("file", file);
        System.out.println(exchangeHouseModel);
        System.out.println(formData);
        String redirectUrl = "";
        if(exchangeHouseModel != null && exchangeCode.equals(exchangeHouseModel.getExchangeCode())){
            redirectUrl = "/" + exchangeHouseModel.getBaseTableName() + "Upload";  //generate dynamic URL from database
        }
        return "forward:" + redirectUrl;
        
    }
}
