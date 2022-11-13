package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.CocModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/qremit")
public class CocController {
    private final CocModelService cocModelService;

    @Autowired
    public CocController(CocModelService cocModelService){
        this.cocModelService = cocModelService;
    }


    @PostMapping("/downloadcoc")
    public ResponseEntity<Resource> downloadFile(@RequestParam("file") String fileNameId, @RequestParam("coc") String fileType ) {
        InputStreamResource file = new InputStreamResource(cocModelService.load(fileNameId, fileType));
        String fileName = "coc.txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/json"))
                .body(file);
    }
}
