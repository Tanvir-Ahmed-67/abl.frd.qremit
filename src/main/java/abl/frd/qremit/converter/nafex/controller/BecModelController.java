package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.BecModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.BecModelService;
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
    
    @Autowired
    public BecModelController(BecModelService becModelService,MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
        this.becModelService = becModelService;
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
        if (BecModelServiceHelper.hasCSVFormat(file)) {
            int extensionIndex = file.getOriginalFilename().lastIndexOf(".");
            try {
                fileInfoModelObject = becModelService.save(file, userId);
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
