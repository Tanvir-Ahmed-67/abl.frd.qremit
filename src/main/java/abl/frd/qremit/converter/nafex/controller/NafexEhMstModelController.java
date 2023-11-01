package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.service.NafexModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    @RequestMapping("/home")
    public String loginSubmit(){
        return "/pages/adminLandingPage";
    }
    @RequestMapping("/logout")
    public String logoutSuccessPage(){
        return "auth-login";
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        //MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = "";
        FileInfoModel fileInfoModelObject;
        if (NafexModelServiceHelper.hasCSVFormat(file)) {
            try {
                fileInfoModelObject = nafexModelService.save(file);
                model.addAttribute("fileInfo", fileInfoModelObject);
                return "downloadPage";

            } catch (Exception e) {
                e.printStackTrace();
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return "downloadPage";
            }
        }
        message = "Please upload a csv file!";
        return "downloadPage";
    }

}
