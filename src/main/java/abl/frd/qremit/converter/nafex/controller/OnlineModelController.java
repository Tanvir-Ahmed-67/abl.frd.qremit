package abl.frd.qremit.converter.nafex.controller;
import abl.frd.qremit.converter.nafex.service.OnlineModelService;
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
    public ResponseEntity<Resource> download_File() {
        InputStreamResource file = new InputStreamResource(onlineModelService.loadAndUpdateUnprocessedOnlineData(0));
        int countRemainingOnlineData = onlineModelService.countRemainingOnlineData();
        String fileName = "Online";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt")
                .header("count", String.valueOf(countRemainingOnlineData))
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
    @GetMapping("/countOnlineAfterDownloadButtonClicked")
    @ResponseBody
    public int countOnlineAfterDownloadButtonClicked(){
        int count = onlineModelService.countUnProcessedOnlineData(0);
        return count;
    }
}
