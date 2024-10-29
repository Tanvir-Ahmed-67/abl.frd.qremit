package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.OnlineModelService;

import java.io.*;
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
    /*
    public ResponseEntity<Resource> download_File() {
        InputStreamResource file = new InputStreamResource(onlineModelService.loadAndUpdateUnprocessedOnlineData(0));
        int countRemainingOnlineData = onlineModelService.countRemainingOnlineData();
        String fileName = "Online";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .header("count", String.valueOf(countRemainingOnlineData))
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }
    */
    
    public ResponseEntity<Resource> download_File() throws IOException {
        InputStream contentStream  = onlineModelService.loadAndUpdateUnprocessedOnlineData(0);
        int countRemainingOnlineData = onlineModelService.countRemainingOnlineData();
        String fileName = "Online.txt";
        String tempFilePath  = CommonService.dirPrefix + CommonService.reportDir + fileName;
        try {
            // Check if InputStream has data and log
            if (contentStream == null) {
                System.out.println("Content stream is null.");
                return ResponseEntity.status(500).body(null); // Server error response
            }

            // Create a ByteArrayOutputStream to hold the data
            //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //contentStream.transferTo(byteArrayOutputStream); // Read all data to byteArrayOutputStream

            byte[] contentBytes = contentStream.readAllBytes();

            // Log the size of data
            System.out.println("Data size after reading contentStream: " + contentBytes.length + " bytes");

            // Write to file
            writeToFile(contentBytes, tempFilePath);

            // Create InputStreamResource from the byte array for the response
            InputStreamResource fileResource = new InputStreamResource(new ByteArrayInputStream(contentBytes));

            // Return the ResponseEntity with the file for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .header("count", String.valueOf(countRemainingOnlineData))
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(fileResource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Return a server error response
        }
        /*
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
            */
        /*
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = onlineModelService.loadAndUpdateUnprocessedOnlineData(0)) {
                System.out.println(onlineModelService.loadAndUpdateUnprocessedOnlineData(0));
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println(outputStream.toString());
            }
        };
        System.out.println(stream);

        int countRemainingAccountPayeeData = onlineModelService.countRemainingOnlineData();
        String fileName = "Account_Payee_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".txt")
                .header("count", String.valueOf(countRemainingAccountPayeeData))
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(stream);
                */
    }

    private void writeToFile(byte[] contentBytes, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            bos.write(contentBytes);
            bos.flush();
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
            System.err.println("Error writing data to file: " + e.getMessage());
            throw e;
        }
    }
    

    @GetMapping("/countOnlineAfterDownloadButtonClicked")
    @ResponseBody
    public int countOnlineAfterDownloadButtonClicked(){
        int count = onlineModelService.countUnProcessedOnlineData(0);
        return count;
    }
}
