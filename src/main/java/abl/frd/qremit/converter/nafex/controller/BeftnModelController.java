package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.BeftnModelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BeftnModelController {
    private final BeftnModelService beftnModelService;
    public BeftnModelController(BeftnModelService beftnModelService){
        this.beftnModelService = beftnModelService;
    }

    @GetMapping("/downloadbeftn/{fileId}/{fileType}")
    public ResponseEntity<Resource> download_File(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(beftnModelService.load(fileId, fileType));
        String fileName = "Beftn_Main_Nafex";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".xlsx")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    @GetMapping("/downloadbeftnMain")
    public ResponseEntity<Resource> downloadMainFile() {
        InputStreamResource file = new InputStreamResource(beftnModelService.loadAndUpdateUnprocessedBeftnMainData("0"));
        String fileName = "Beftn_Main";  // Have to attch date with file name here.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".xlsx")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/downloadBeftnIncentive/{fileId}/{fileType}")
    public ResponseEntity<Resource> downloadIncentiveFile(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(beftnModelService.loadIncentive(fileId, fileType));
        String fileName = "Beftn_Incentive_Nafex";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".xlsx")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    @GetMapping("/downloadBeftnIncentive")
    public ResponseEntity<Resource> downloadIncentiveFile() {
        InputStreamResource file = new InputStreamResource(beftnModelService.loadAndUpdateUnprocessedBeftnIncentiveData("0"));
        String fileName = "Beftn_Main";  // Have to attch date with file name here.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".xlsx")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
