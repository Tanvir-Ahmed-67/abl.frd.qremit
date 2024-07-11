package abl.frd.qremit.converter.nafex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
    public UtilsController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    
    @GetMapping("/index")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam String id){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        int showDropDown = 0;
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(id);
        switch (id) {
            case "7010226":
                showDropDown = 1;
                break;
            case "7010299":
                showDropDown = 1;
                break;
            default:
                break;
        }
        model.addAttribute("showDropDown", showDropDown);
        model.addAttribute("exchangeHouseModel", exchangeHouseModel);
        return "/fragments/file_upload_form";
    }
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails,@RequestParam("file") MultipartFile file,@RequestParam("exName") String exName, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        model.addAttribute("exName", exName);
        model.addAttribute("file", file);

        String redirectUrl ="";
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exName);
        if(exName.equals(exchangeHouseModel.getExchangeCode()))  redirectUrl = "/" + exchangeHouseModel.getBaseTableName() + "Upload";  //generate dynamic URL from database    
        return "forward:" + redirectUrl;
    }
    
}
