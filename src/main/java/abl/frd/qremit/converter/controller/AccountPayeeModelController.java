package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.service.AccountPayeeModelService;
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
public class AccountPayeeModelController {
    private final AccountPayeeModelService accountPayeeModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    FileDownloadService fileDownloadService;
    @Autowired
    public AccountPayeeModelController(AccountPayeeModelService accountPayeeModelService){
        this.accountPayeeModelService = accountPayeeModelService;
    }
    @GetMapping(value="/downloadaccountpayee", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadFile() throws IOException {
        Map<String, Object> resp = new HashMap<>();
        ByteArrayInputStream contentStream  = accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0);
        int countRemaining = accountPayeeModelService.countRemainingAccountPayeeData();
        String fileName = CommonService.generateDynamicFileName("Account_Payee_", ".txt");
        resp = commonService.generateFile(contentStream, countRemaining, fileName);
        if((Integer) resp.get("err") == 0){
            fileDownloadService.add("2", fileName, resp.get("url").toString());
        }
        return ResponseEntity.ok(resp);
    }
}
