package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.*;
import abl.frd.qremit.converter.service.ApiT24ModelService;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.DynamicOperationService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Controller
public class ApiT24ModelController {
    @Autowired
    private DynamicOperationService dynamicOperationService;
    @Autowired
    private ApiT24ModelService apit24ModelService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @PostMapping("/api_t24Upload")
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
        if (CommonService.hasCSVFormat(file)) {
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                try {
                    Map<String, Object> resp = apit24ModelService.save(file, userId, exchangeCode);
                    model = CommonService.viewUploadStatus(resp, model);
                    model.addAttribute("apiBtn", 1);
                    model.addAttribute("apiUrl", "/apit24transfer");
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
    @PostMapping("/apit24transfer")
    @ResponseBody
    public Map<String, Object> transferApiT24Data(@RequestParam("id") String id){
        if(("").matches(id))   return CommonService.getResp(1, "Please select Id", null);
        return dynamicOperationService.transferApiT24Data(CommonService.convertStringToInt(id));
    }
}
