package abl.frd.qremit.converter.nafex.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.TemporaryReportService;

@Controller
public class TemporaryReportController {
    @Autowired
    TemporaryReportService temporaryReportService;
    private final MyUserDetailsService myUserDetailsService;

    public TemporaryReportController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }

    @GetMapping("/generateTemporaryReport")
    public ResponseEntity<Map<String, Object>> generateTemporaryReport(@AuthenticationPrincipal MyUserDetails userDetails){
        Map<String, Object> resp = new HashMap<>();
        temporaryReportService.processTemporaryReport();
        return ResponseEntity.ok(resp);
    }

}
