package abl.frd.qremit.converter.nafex.controller;

import java.util.*;
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
import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.service.CommonService;
import abl.frd.qremit.converter.nafex.service.DynamicOperationService;
import abl.frd.qremit.converter.nafex.service.ErrorDataModelService;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.LogModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;

@Controller
@RequestMapping("/error")
public class ErrorDataController {
    private final MyUserDetailsService myUserDetailsService;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    @Autowired
    LogModelService logModelService;
    @Autowired
    DynamicOperationService dynamicOperationService;
    
    public ErrorDataController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    @GetMapping("/editForm/{id}")
    public String editErrorDataForm(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") String id, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        ErrorDataModel errorDataModel = errorDataModelService.findErrorModelById(Integer.parseInt(id));
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

    @GetMapping("/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveErrorDataById(@RequestParam String id){
        Map<String, Object> resp = new HashMap<>();
        int errorDataId = Integer.parseInt(id);
        ErrorDataModel errorDataModel = errorDataModelService.findErrorModelById(errorDataId);
        if(errorDataModel == null)  return ResponseEntity.ok(CommonService.getResp(1, "No data found following Error Model", null));
        if(errorDataModel.getUpdateStatus() != 1)   return ResponseEntity.ok(CommonService.getResp(1, "Invalid Type for approve data", null));  //for approve status must be 1
        if(CommonService.checkEmptyString(id)){
            resp = CommonService.getResp(1, "Invalid Id", null);
            return ResponseEntity.ok(resp);
        }
        List<Map<String, Object>> logData =  logModelService.findLogModelByErrorDataId(id);
        Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
        resp = dynamicOperationService.transferErrorData(updatedDataMap);
        Integer err = (Integer) resp.get("err");
        if(err == 0){
            errorDataModelService.updateErrorDataModelUpdateStatus(errorDataId,2);
            resp = CommonService.getResp(0, "Information saved succesfully", null);
        }
        return ResponseEntity.ok(resp);
    }

    
    
}
