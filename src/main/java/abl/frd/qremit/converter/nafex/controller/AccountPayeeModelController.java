package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.service.AccountPayeeModelService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
public class AccountPayeeModelController {
    private final AccountPayeeModelService accountPayeeModelService;

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
    @GetMapping("/downloadaccountpayee")
    /*
    public ResponseEntity<Resource> download_File() {
        InputStreamResource file = new InputStreamResource(accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0));
        int countRemainingAccountPayeeData = accountPayeeModelService.countRemainingAccountPayeeData();
        String fileName = "Account_Payee";  // Have to attch date with file name here.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .header("count", String.valueOf(countRemainingAccountPayeeData))
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    */
    
    public void downloadFile() {
        InputStreamResource file = new InputStreamResource(accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0));
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            System.out.println("InputStreamResource Content:\n" + content);
        } catch (Exception e) {
            System.err.println("Error reading InputStreamResource: " + e.getMessage());
        }
        /*
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0)) {
                System.out.println(accountPayeeModelService.loadAndUpdateUnprocessedAccountPayeeData(0));
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                //System.out.println(outputStream.to);
            }
        };
        System.out.println(stream);

        int countRemainingAccountPayeeData = accountPayeeModelService.countRemainingAccountPayeeData();
        String fileName = "Account_Payee_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".txt")
                .header("count", String.valueOf(countRemainingAccountPayeeData))
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(stream);
                */
    }
}
