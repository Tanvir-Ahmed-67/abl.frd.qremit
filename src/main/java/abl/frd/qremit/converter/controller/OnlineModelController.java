package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.OnlineModelService;
import java.io.*;
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
public class OnlineModelController {
    private final OnlineModelService onlineModelService;
    @Autowired
    public OnlineModelController(OnlineModelService onlineModelService){
        this.onlineModelService = onlineModelService;
    }

    @GetMapping("/downloadonline/{fileId}/{fileType}")
    public ResponseEntity<Resource> download_File(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(onlineModelService.load(fileId, fileType));
        String fileName = "Online";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    
    @GetMapping("/downloadonline")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = onlineModelService.loadAndUpdateUnprocessedOnlineData(0);
        int countRemaining = onlineModelService.countRemainingOnlineData();
        String fileName = CommonService.generateDynamicFileName("Online", ".txt");
        resp = CommonService.generateFile(contentStream, countRemaining, fileName);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/countOnlineAfterDownloadButtonClicked")
    @ResponseBody
    public int countOnlineAfterDownloadButtonClicked(){
        int count = onlineModelService.countUnProcessedOnlineData(0);
        return count;
    }
}