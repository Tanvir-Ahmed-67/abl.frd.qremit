package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.NafexModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class NafexEhMstModelController {
    private final NafexModelService nafexModelService;

    @Autowired
    public NafexEhMstModelController(NafexModelService nafexModelService){
        this.nafexModelService = nafexModelService;
    }
    @RequestMapping("/login")
    public String loginPage(){
        return "auth-login";
    }
    @RequestMapping("/super-admin-home-page")
    public String loginSubmitSuperAdmin(){ return "/layouts/dashboard"; }
    @RequestMapping("/admin-home-page")
    public String loginSubmitAdmin(){ return "/layouts/dashboard"; }
    @RequestMapping("/user-home-page")
    public String loginSubmitUser(){ return "/layouts/dashboard"; }
    @RequestMapping("/home")
    public String loginSubmit(){
        return "/layouts/dashboard";
    }
    @RequestMapping("/logout")
    public String logoutSuccessPage(){
        return "auth-login";
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
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
        if (NafexModelServiceHelper.hasCSVFormat(file)) {
            try {
                fileInfoModelObject = nafexModelService.save(file, userId);
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
    @GetMapping("/adminDashboard")
    @ResponseBody
    public String loadAdminDashboard(Model model){
        List<Integer> count = nafexModelService.CountAllFourTypesOfData();
        model.addAttribute("onlineCount", count.get(0));
        model.addAttribute("cocCount", count.get(1));
        model.addAttribute("accountPayeeCount", count.get(2));
        model.addAttribute("beftnCount", count.get(3));
        System.out.println("Admin Dashboard from controller"+count.toString());
        return "/adminDashboard";
    }
}
