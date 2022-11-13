package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.AccountPayeeModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/qremit")
public class AccountPayeeModelController {
    private final AccountPayeeModelService accountPayeeModelService;

    @Autowired
    public AccountPayeeModelController(AccountPayeeModelService accountPayeeModelService){
        this.accountPayeeModelService = accountPayeeModelService;
    }

    @GetMapping("/downloadaccountpayee/{fileId}/{fileType}")
    public ResponseEntity<Resource> download_File(@PathVariable String fileId, @PathVariable String fileType) {
        InputStreamResource file = new InputStreamResource(accountPayeeModelService.load(fileId, fileType));
        String fileName = "account_payee_nafex";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
