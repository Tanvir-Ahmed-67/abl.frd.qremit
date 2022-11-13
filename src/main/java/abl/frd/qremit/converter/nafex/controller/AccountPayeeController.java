package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.AccountPayeeModelService;
import abl.frd.qremit.converter.nafex.service.CocModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/qremit")
public class AccountPayeeController {
    private final AccountPayeeModelService accountPayeeModelService;

    @Autowired
    public AccountPayeeController(AccountPayeeModelService accountPayeeModelService){
        this.accountPayeeModelService = accountPayeeModelService;
    }

    @PostMapping("/downloadaccountpayee")
    public ResponseEntity<Resource> downloadFile(@RequestParam("file") String fileNameId, @RequestParam("accountpayee") String fileType ) {
        InputStreamResource file = new InputStreamResource(accountPayeeModelService.load(fileNameId, fileType));
        String fileName = "accountpayee.txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .contentType(MediaType.parseMediaType("application/json"))
                .body(file);
    }
}
