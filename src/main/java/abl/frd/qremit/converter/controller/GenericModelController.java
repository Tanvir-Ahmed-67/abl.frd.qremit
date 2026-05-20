package abl.frd.qremit.converter.controller;
import java.util.*;

import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.GenericModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.service.GenericModelService;
import abl.frd.qremit.converter.service.HorizonService;

@Controller
public class GenericModelController {
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    GenericModelService genericModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    HorizonService horizonService;

    @PostMapping("/genericUpload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, @ModelAttribute("fileType") String fileType,
        @ModelAttribute("exchangeCode") String exchangeCode, @RequestParam("nrtaCode") String nrtaCode, @RequestParam("tbl") String tbl, Model model) {
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        int userId = 000000000;
        // Getting Logged In user Details in this block
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
        }
        String message = "";
        Map<String, Object> resp = new HashMap<>();
        if (CommonService.hasCSVFormat(file)){
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                try{
                    if(exchangeCode.equals("7010313"))  resp = horizonService.save(file, userId, exchangeCode, fileType, nrtaCode, tbl);
                    else resp = genericModelService.save(file, userId, exchangeCode, nrtaCode, tbl, GenericModel.class);
                    model = CommonService.viewUploadStatus(resp, model);
                    return CommonService.uploadSuccesPage;
                }catch(Exception e){
                    e.printStackTrace();
                    model.addAttribute("message", e.getMessage());
                    return CommonService.uploadSuccesPage;
                }
            }
            message = "File With The Name "+ file.getOriginalFilename() +" Already Exists !!";
            model.addAttribute("message", message);
            return CommonService.uploadSuccesPage;
        }
        model.addAttribute("message", "Please Upload a CSV File!");
        return CommonService.uploadSuccesPage;
    }  

}
