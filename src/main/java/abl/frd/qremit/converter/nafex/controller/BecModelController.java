package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.helper.BecModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.service.BecModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/bec")
public class BecModelController {
    private final BecModelService becModelService;

    @Autowired
    public BecModelController(BecModelService becModelService){
        this.becModelService = becModelService;
    }

    @GetMapping(value = "/index")
    public String homePage() {

        System.out.println("Test");
        return "bec_home";
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String message = "";
        String count ="";
        FileInfoModel fileInfoModelObject;
        if (BecModelServiceHelper.hasCSVFormat(file)) {
            int extensionIndex = file.getOriginalFilename().lastIndexOf(".");
            try {
                fileInfoModelObject = becModelService.save(file);
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
