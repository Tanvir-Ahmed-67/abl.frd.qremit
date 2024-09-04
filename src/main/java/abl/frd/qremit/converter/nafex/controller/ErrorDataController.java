package abl.frd.qremit.converter.nafex.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.LogModel;
import abl.frd.qremit.converter.nafex.repository.LogModelRepository;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.ErrorDataModelService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;

@Controller
@RequestMapping("/error")
public class ErrorDataController {
    private final MyUserDetailsService myUserDetailsService;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    
    public ErrorDataController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    @GetMapping("/editForm/{id}")
    public String editErrorDataForm(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") String id, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        ErrorDataModel errorDataModel = errorDataModelService.findErrorModelById(Long.valueOf(id));
        //System.out.println(errorDataModel.toString());
        model.addAttribute("errorDataModel", errorDataModel);
        return "/pages/user/editErrorForm";
    }
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateErrorDataById(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam Map<String, String> formData, Model model, HttpServletRequest request){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        //Map<String, Object> resp = new HashMap<>();
        formData.remove("_csrf");
        formData.remove("_csrf_header");
        Map<String, Object> resp = errorDataModelService.processUpdateErrorDataById(formData, request);
        return ResponseEntity.ok(resp);
    }

    
    
}
