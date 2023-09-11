package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.BeftnModelService;
import abl.frd.qremit.converter.nafex.service.CocModelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/qremit")
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

    @GetMapping("/downloadBeftnIncentive/{fileId}/{fileType}")
    public ResponseEntity<Resource> downloadIncentiveFile(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(beftnModelService.loadIncentive(fileId, fileType));
        String fileName = "Beftn_Incentive_Nafex";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".xlsx")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
