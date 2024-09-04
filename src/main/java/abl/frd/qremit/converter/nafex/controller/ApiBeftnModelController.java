package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.ApiBeftnModelService;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.DynamicOperationService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ApiBeftnModelController {
    @Autowired
    private DynamicOperationService dynamicOperationService;
    @Autowired
    private ApiBeftnModelService apiBeftnModelService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @PostMapping("/apibeftnUpload")
    public String saveData(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, @ModelAttribute("exchangeCode") String exchangeCode, Model model) {
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
        FileInfoModel fileInfoModelObject;
        if (commonService.hasCSVFormat(file)) {
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                try {
                    fileInfoModelObject = apiBeftnModelService.save(file, userId, exchangeCode);
                    if(fileInfoModelObject!=null){
                        model.addAttribute("fileInfo", fileInfoModelObject);
                        return commonService.uploadApiSuccessPage;
                    }
                    else{
                        message = "All Data From Your Selected File Already Exists!";
                        model.addAttribute("message", message);
                        return commonService.uploadApiSuccessPage;
                    }
                }
                catch (IllegalArgumentException e) {
                    message = e.getMessage();
                    model.addAttribute("message", message);
                    return commonService.uploadApiSuccessPage;
                }
                catch (Exception e) {
                    message = "Could Not Upload The File: " + file.getOriginalFilename() +"";
                    model.addAttribute("message", message);
                    return commonService.uploadApiSuccessPage;
                }
            }
            message = "File With The Name "+ file.getOriginalFilename() +" Already Exists !!";
            model.addAttribute("message", message);
            return commonService.uploadApiSuccessPage;
        }
        message = "Please Upload a CSV File!";
        model.addAttribute("message", message);
        return commonService.uploadApiSuccessPage;
    }

    @PostMapping("/apibeftntransfer")
    public String transferApiBeftnData(){
        dynamicOperationService.transferApiBeftnData();
        return "redirect:/user-home-page";
    }
}
