package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.CocModelService;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.FileDownloadService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
public class CocModelController {
    private final CocModelService cocModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    @Autowired
    public CocModelController(CocModelService cocModelService){
        this.cocModelService = cocModelService;
    }

    @GetMapping(value="/downloadcoc", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = cocModelService.loadAndUpdateUnprocessedCocData(0,0);
        int countRemaining = cocModelService.countRemainingCocData();
        String fileName = CommonService.generateDynamicFileName("COC_", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("4", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }
}
