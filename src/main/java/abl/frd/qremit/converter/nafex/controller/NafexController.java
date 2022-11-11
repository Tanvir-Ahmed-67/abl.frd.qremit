package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.ResponseMessage;
import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.service.NafexModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/qremit")
public class NafexController {
    private final NafexModelService nafexModelService;

    @Autowired
    public NafexController(NafexModelService nafexModelService){
        this.nafexModelService = nafexModelService;
    }

    @GetMapping(value = "/index")
    public String homePage() {
        System.out.println("inside controller");
        return "nafex";
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String message = "";
        String count ="";
        FileInfoModel fileInfoModelObject;
        if (NafexModelServiceHelper.hasCSVFormat(file)) {
            int extensionIndex = file.getOriginalFilename().lastIndexOf(".");
            String fileNameWithoutExtension = file.getOriginalFilename().substring(0,extensionIndex);
            try {
                fileInfoModelObject = nafexModelService.save(file);
                model.addAttribute("fileInfo", fileInfoModelObject);
                //message ="Uploaded the file successfully: " + file.getOriginalFilename();
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/qremit/download/")
                        .path(fileNameWithoutExtension+".txt")
                        .toUriString();

                return "newPage";

                //return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message,fileDownloadUri));
            } catch (Exception e) {
                e.printStackTrace();
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return "newPage";
                //return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(count,""));
            }
        }
        message = "Please upload a csv file!";
        return "newPage";
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message,""));
    }

}
