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
import java.util.*;

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

    @PostMapping("/api_beftnUpload")
    public String saveData(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, @ModelAttribute("exchangeCode") String exchangeCode, 
        Model model) {
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
        if (CommonService.hasCSVFormat(file)) {
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                try {
                    Map<String, Object> resp = apiBeftnModelService.save(file, userId, exchangeCode);
                    model = CommonService.viewUploadStatus(resp, model);
                    model.addAttribute("apiBtn", 1);
                    model.addAttribute("apiUrl", "/apibeftntransfer");
                    return CommonService.uploadSuccesPage;
                }
                catch (IllegalArgumentException e) {
                    message = e.getMessage();
                    model.addAttribute("message", message);
                    return CommonService.uploadSuccesPage;
                }
                catch (Exception e) {
                    message = "Could Not Upload The File: " + file.getOriginalFilename() +"";
                    model.addAttribute("message", message);
                    return CommonService.uploadSuccesPage;
                }
            }
            message = "File With The Name "+ file.getOriginalFilename() +" Already Exists !!";
            model.addAttribute("message", message);
            return CommonService.uploadSuccesPage;
        }
        message = "Please Upload a CSV File!";
        model.addAttribute("message", message);
        return CommonService.uploadSuccesPage;
    }

    @PostMapping("/apibeftntransfer")
    @ResponseBody
    public Map<String, Object> transferApiBeftnData(@RequestParam("id") String id){
        if(("").matches(id))   return CommonService.getResp(1, "Please select Id", null);
        return dynamicOperationService.transferApiBeftnData(Integer.parseInt(id));
        //return "redirect:/user-home-page";
    }
}
