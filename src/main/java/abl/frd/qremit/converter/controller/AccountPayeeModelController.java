package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.AccountPayeeModelService;
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
public class AccountPayeeModelController {
    private final AccountPayeeModelService accountPayeeModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    public AccountPayeeModelController(AccountPayeeModelService accountPayeeModelService){
        this.accountPayeeModelService = accountPayeeModelService;
    }

    @GetMapping("/downloadaccountpayee/{fileId}/{fileType}")
    public ResponseEntity<Resource> download_File(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(accountPayeeModelService.load(fileId, fileType));
        String fileName = "Account_Payee";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    @GetMapping(value="/downloadaccountpayee", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0);
        int countRemaining = accountPayeeModelService.countRemainingAccountPayeeData();
        String fileName = CommonService.generateDynamicFileName("Account_Payee_", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        return ResponseEntity.ok(resp);
    }
}
