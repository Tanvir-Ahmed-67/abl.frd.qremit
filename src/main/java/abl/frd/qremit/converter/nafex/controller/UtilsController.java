package abl.frd.qremit.converter.nafex.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;



@Controller
@RequestMapping("/utils")
public class UtilsController {
    private final MyUserDetailsService myUserDetailsService;
    public UtilsController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    
    @GetMapping("/index")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        return "/fragments/file_upload_form";
    }
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails,@RequestParam("file") MultipartFile file,@RequestParam("exName") String exName, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));

        model.addAttribute("exName", exName);
        model.addAttribute("file", file);
        
        String redirectUrl ="";
        switch(exName){
            case "7010234":
                redirectUrl  = "/nafexUpload";
                break;
            case "7010209":
                redirectUrl  = "/becUpload";
                break;
            case "7010231":
                redirectUrl  = "/muzainiUpload";
                break;
            case "7010226":
                redirectUrl  = "/agexSingaporeUpload";
                break;
            case "7010299":
                redirectUrl  = "/ezRemitUpload";
                break;
        }
        return "forward:" + redirectUrl;
    }

    

    
}
