package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.BecModelService;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class BecModelController {
    private final MyUserDetailsService myUserDetailsService;
    private final BecModelService becModelService;
    private final CommonService commonService;
    
    @Autowired
    public BecModelController(BecModelService becModelService,MyUserDetailsService myUserDetailsService, CommonService commonService){
        this.myUserDetailsService = myUserDetailsService;
        this.becModelService = becModelService;
        this.commonService = commonService;
    }

    @PostMapping("/becUpload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, Model model) {
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
            if(!commonService.ifFileExist(file.getOriginalFilename())) {
                try {
                    fileInfoModelObject = becModelService.save(file, userId);
                    if(fileInfoModelObject!=null){
                        model.addAttribute("fileInfo", fileInfoModelObject);
                        return commonService.uploadSuccesPage;
                    }
                    else{
                        message = "All Data From Your Selected File Already Exists!";
                        model.addAttribute("message", message);
                        return commonService.uploadSuccesPage;
                    }
                } catch (IllegalArgumentException e) {
                    message = e.getMessage();
                    model.addAttribute("message", message);
                    return commonService.uploadSuccesPage;
                }
                catch (Exception e) {
                    message = "Could Not Upload The File: " + file.getOriginalFilename() +"";
                    model.addAttribute("message", message);
                    return commonService.uploadSuccesPage;
                }
            }
            message = "File With The Name "+ file.getOriginalFilename() +" Already Exists !!";
            model.addAttribute("message", message);
            return commonService.uploadSuccesPage;
        }
        message = "Please Upload a CSV File!";
        model.addAttribute("message", message);
        return commonService.uploadSuccesPage;
    }
}
