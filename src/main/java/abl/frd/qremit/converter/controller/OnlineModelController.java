package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.FileDownloadService;
import abl.frd.qremit.converter.service.OnlineModelService;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OnlineModelController {
    private final OnlineModelService onlineModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    @Autowired
    public OnlineModelController(OnlineModelService onlineModelService){
        this.onlineModelService = onlineModelService;
    }

    @GetMapping(value="/downloadonline", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = onlineModelService.loadAndUpdateUnprocessedOnlineData(0);
        int countRemaining = onlineModelService.countRemainingOnlineData();
        String fileName = CommonService.generateDynamicFileName("Online_", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("1", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/countOnlineAfterDownloadButtonClicked")
    @ResponseBody
    public int countOnlineAfterDownloadButtonClicked(){
        int count = onlineModelService.countUnProcessedOnlineData(0);
        return count;
    }
}
