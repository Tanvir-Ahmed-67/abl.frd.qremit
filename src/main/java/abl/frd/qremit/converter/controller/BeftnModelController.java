package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.BeftnModelService;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.FileDownloadService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BeftnModelController {
    private final BeftnModelService beftnModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    public BeftnModelController(BeftnModelService beftnModelService){
        this.beftnModelService = beftnModelService;
    }

    @GetMapping("/downloadbeftnMain")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadMainFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = beftnModelService.loadAndUpdateUnprocessedBeftnMainData(0);
        int countRemaining = beftnModelService.countRemainingBeftnDataMain();
        String fileName = CommonService.generateDynamicFileName("Beftn_Main_", ".xlsx");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("3", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/downloadBeftnIncentive")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadIncentiveFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = beftnModelService.loadAndUpdateUnprocessedBeftnIncentiveData(0);
        int countRemaining = beftnModelService.countRemainingBeftnDataIncentive();
        String fileName = CommonService.generateDynamicFileName("Beftn_Incentive_", ".xlsx");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("3", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/notProcessingBeftnIncentive", produces = "application/json")
    public ResponseEntity<Map<String, Object>> calculateNotProcessingBeftnIncentive(){
        Map<String, Object> resp = beftnModelService.calculateNotProcessingBeftnIncentive();
        return ResponseEntity.ok(resp);
    }


}
