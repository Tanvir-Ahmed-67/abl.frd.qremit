package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.BecModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.service.BecModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;


@Controller
//@RequestMapping("/bec")
public class BecModelController {
    private final BecModelService becModelService;
    private final MyUserDetailsService myUserDetailsService;
    
    @Autowired
    public BecModelController(BecModelService becModelService,MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
        this.becModelService = becModelService;
    }

    @GetMapping(value = "/index")
    public String homePage() {

        System.out.println("Test");
        return "bec_home";
    }
    
    @PostMapping("/becUpload")
    public String uploadFile(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute("file") MultipartFile file, Model model) {
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));  

        String message = "";
        String count ="";
        FileInfoModel fileInfoModelObject;
        if (BecModelServiceHelper.hasCSVFormat(file)) {
            int extensionIndex = file.getOriginalFilename().lastIndexOf(".");
            try {
                fileInfoModelObject = becModelService.save(file);
                model.addAttribute("fileInfo", fileInfoModelObject);
                return "/pages/user/userUploadSuccessPage";
            } catch (Exception e) {
                e.printStackTrace();
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return "/pages/user/userUploadSuccessPage";
            }
        }
        message = "Please upload a csv file!";
        return "/pages/user/userUploadSuccessPage";
    }

}
