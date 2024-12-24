package abl.frd.qremit.converter.controller;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.ErrorDataModelService;
import abl.frd.qremit.converter.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.service.FileInfoModelService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.DynamicOperationService;
import abl.frd.qremit.converter.service.LogModelService;

@SuppressWarnings("unchecked")
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
    @Autowired
    FileInfoModelService fileInfoModelService;
    
    public ErrorDataController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }
    @GetMapping("/editForm/{id}")
    public String editErrorDataForm(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") String id, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        ErrorDataModel errorDataModel = errorDataModelService.findErrorModelById(CommonService.convertStringToInt(id));
        //System.out.println(errorDataModel.toString());
        model.addAttribute("errorDataModel", errorDataModel);
        return "pages/user/editErrorForm";
    }
    @PostMapping(value="/update", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateErrorDataById(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam Map<String, String> formData, Model model, HttpServletRequest request){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        Map<String, Object> resp = new HashMap<>();
        formData.remove("_csrf");
        formData.remove("_csrf_header");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
            resp = errorDataModelService.processUpdateErrorDataById(formData, request, userId);
        }
        
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/viewError/{id}")
    public String viewError(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") String id, Model model){
        //model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        String page = "pages/admin/viewError";
        Map<String, Object> resp = getLogData(id);
        if((Integer) resp.get("err") == 1){
            model.addAttribute("message", (String) resp.get("msg"));
            return page;
        }
        List<Map<String, Object>> logData = (List<Map<String, Object>>) resp.get("data");
        Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
        Map<String, Object> oldDataMap = logModelService.fetchLogDataByKey(logData, "oldData");
        model.addAttribute("updatedDataMap", updatedDataMap);
        model.addAttribute("oldDataMap",oldDataMap);
        model.addAttribute("id", id);
        return page;
    }

    @PostMapping(value="/approve", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveErrorDataById(@RequestParam String id){
        int errorDataId = CommonService.convertStringToInt(id);
        Map<String, Object> resp = getLogData(id);
        if((Integer) resp.get("err") == 1)  return ResponseEntity.ok(resp);
        List<Map<String, Object>> logData = (List<Map<String, Object>>) resp.get("data");
        Map<String, Object> updatedDataMap = logModelService.fetchLogDataByKey(logData, "updatedData");
        resp = dynamicOperationService.transferErrorData(updatedDataMap);
        Integer err = (Integer) resp.get("err");
        if(err == 0){
            errorDataModelService.updateErrorDataModelUpdateStatus(errorDataId,2);
            resp = CommonService.getResp(0, "Information saved succesfully", null);
        }
        return ResponseEntity.ok(resp);
    }

    public Map<String, Object> getLogData(String id){
        Map<String, Object> resp = new HashMap<>();
        if(CommonService.checkEmptyString(id)){
            resp = CommonService.getResp(1, "Invalid Id", null);
            return resp;
        }
        int errorDataId = CommonService.convertStringToInt(id);
        ErrorDataModel errorDataModel = errorDataModelService.findErrorModelById(errorDataId);
        if(errorDataModel == null)  return CommonService.getResp(1, "No data found following Error Model", null);
        if(errorDataModel.getUpdateStatus() != 1)   return CommonService.getResp(1, "Invalid Type for approve data", null);  //for approve status must be 1
        List<Map<String, Object>> logData =  logModelService.findLogModelByErrorDataId(id);
        return CommonService.getResp(0, "", logData);
    }

    @DeleteMapping(value="/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteErrorDataById(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable int id, HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        int userId = (int) userData.get("userid");
        if(role.get("isAdmin") == 1){
            userId = (int) userData.get("adminUserId");
        }
        
        Map<String, Object> resp = errorDataModelService.deleteErrorDataById(id, request, userId);
        if((Integer) resp.get("err") == 0){
            int fileInfoModelId = (Integer) resp.get("fileInfoModelId");
            FileInfoModel fileInfoModel = fileInfoModelService.findFileInfoModelById(fileInfoModelId);
            int errorCount = fileInfoModel.getErrorCount();
            errorCount = errorCount - 1;
            fileInfoModelService.updateErrorCountById(fileInfoModelId, errorCount);
        }
        return ResponseEntity.ok(resp);
    }
}
