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
public class CocModelController {
    private final CocModelService cocModelService;

    @Autowired
    public CocModelController(CocModelService cocModelService){
        this.cocModelService = cocModelService;
    }

    @GetMapping("/downloadcoc/{fileId}/{fileType}")
    public ResponseEntity<Resource> download_File(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(cocModelService.load(fileId, fileType));
        String fileName = "Coc";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    @GetMapping("/downloadcoc")
    public ResponseEntity<Resource> download_File() {
        InputStreamResource file = new InputStreamResource(cocModelService.loadAndUpdateUnprocessedCocData(0,0));
        int countRemainingCocData = cocModelService.countRemainingCocData();
        String fileName = "Coc";  // Have to attch date with file name here.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .header("count", String.valueOf(countRemainingCocData))
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
