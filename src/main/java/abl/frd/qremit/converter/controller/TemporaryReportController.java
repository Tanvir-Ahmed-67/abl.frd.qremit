package abl.frd.qremit.converter.controller;
import java.util.*;

import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.TemporaryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemporaryReportController {
    @Autowired
    TemporaryReportService temporaryReportService;

    public TemporaryReportController(){
    }

    @GetMapping(value="/generateTemporaryReport", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateTemporaryReport(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam(defaultValue = "") String date){
        Map<String, Object> resp = new HashMap<>();
        if(date.isEmpty())  date = CommonService.getCurrentDate("yyyy-MM-dd");
        resp = temporaryReportService.processTemporaryReport(date);
        return ResponseEntity.ok(resp);
    }

}
