package abl.frd.qremit.converter.controller;

import abl.frd.qremit.converter.service.CocModelService;
import abl.frd.qremit.converter.service.CommonService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

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
    CommonService commonService;
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
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = cocModelService.loadAndUpdateUnprocessedCocData(0,0);
        int countRemaining = cocModelService.countRemainingCocData();
        String fileName = CommonService.generateDynamicFileName("COC", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        return ResponseEntity.ok(resp);
    }
}
