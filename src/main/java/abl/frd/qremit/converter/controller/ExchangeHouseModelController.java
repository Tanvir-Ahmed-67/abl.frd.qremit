package abl.frd.qremit.converter.controller;

import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.ExchangeHouseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;

@Controller
public class ExchangeHouseModelController {
    @Autowired
    private final ExchangeHouseModelService exchangeHouseModelService;

    public ExchangeHouseModelController(ExchangeHouseModelService exchangeHouseModelService){
        this.exchangeHouseModelService = exchangeHouseModelService;
    }

    @GetMapping(value ="/getAllExchangeHouse", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllExchangeHouse(Model model,@RequestParam(defaultValue = "") String activeStatus){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN") || authorityName.equals("ROLE_ADMIN")){
                List<ExchangeHouseModel> exchangeHouseModelList;
                int checkInactive = (activeStatus.equals("2") && authorityName.equals("ROLE_SUPERADMIN")) ? 1:0;
                if(checkInactive == 1){
                    exchangeHouseModelList = exchangeHouseModelService.loadAllInactiveExchangeHouse();
                }else exchangeHouseModelList = exchangeHouseModelService.loadAllExchangeHouse();
                int i = 1;
                for(ExchangeHouseModel exchangeHouseModel: exchangeHouseModelList){
                    Map<String, Object> dataMap = new HashMap<>();
                    int id = exchangeHouseModel.getId();
                    String status = (exchangeHouseModel.getActiveStatus() == 1) ? "Active" : CommonService.generateClassForText("Inactive","text-danger fw-bold");;
                    String action = "";
                    if(checkInactive == 1){
                        action = CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm activate_exchange",String.valueOf(id),"Activate");
                    }else action = CommonService.generateTemplateBtn("template-editBtn.txt","/exchangeHouseEditForm/" + id,"btn-info btn-sm edit_exchange text-white",String.valueOf(id),"Edit");
                    dataMap.put("sl", i++);
                    dataMap.put("exchangeCode", exchangeHouseModel.getExchangeCode());
                    dataMap.put("exchangeName", exchangeHouseModel.getExchangeName());
                    dataMap.put("exchangeShortName", exchangeHouseModel.getExchangeShortName());
                    dataMap.put("nrtaCode", exchangeHouseModel.getNrtaCode());
                    dataMap.put("status", status);
                    dataMap.put("action", action);
                    dataList.add(dataMap);
                }
                resp.put("data",dataList);
            }else resp = CommonService.getResp(1, "You are not allowed to access this page", null);
        }
        return ResponseEntity.ok(resp);
    }
    @RequestMapping("/newExchangeHouseCreationForm")
    public String showNewExchangeHouseCreateFromAdmin(Model model){
        model.addAttribute("exchangeHouse", new ExchangeHouseModel());
        return "pages/admin/adminNewExchangeHouseEntryForm";
    }
    @RequestMapping(value = "/createNewExchange", method = RequestMethod.POST)
    public String submitNewExchangeHouseCreateFromAdmin(ExchangeHouseModel exchangeHouseModel, RedirectAttributes ra){
        exchangeHouseModel.setActiveStatus(0);
        try{
            exchangeHouseModelService.insertNewExchangeHouse(exchangeHouseModel);
            ra.addFlashAttribute("message","New Exchange House has been created successfully");
        }
        catch (Exception e){
            e.getMessage();
        }
        return "redirect:/viewAllExchangeHouse";
    }
    @RequestMapping("/showInactiveExchangeHouse")
    public String showInactiveExchangeHouseSuperAdmin(Model model){
        List<ExchangeHouseModel> inactiveExchangeHouseModelList;
        inactiveExchangeHouseModelList = exchangeHouseModelService.loadAllInactiveExchangeHouse();
        model.addAttribute("inactiveExchangeHouseList", inactiveExchangeHouseModelList);
        return "pages/superAdmin/superAdminInactiveExchangeHouseListPage";
    }

    @RequestMapping(value="/activateExchangeHouse/{id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> activateInactiveExchangeHouse(Model model, @PathVariable(required = true, name = "id") String id) {
        Map<String, Object> resp = new HashMap<>();
        int idInIntegerFormat = Integer.parseInt(id);
        if(exchangeHouseModelService.updateInactiveExchangeHouse(idInIntegerFormat)){
            //ra.addFlashAttribute("message","Exchange House has been activated successfully");
            resp = CommonService.getResp(0, "Exchange House has been activated successfully", null);
        }else resp = CommonService.getResp(1, "Exchange House has not activated", null);
        return resp;
    }
    @RequestMapping(value="/exchangeHouseEditForm/{id}", method= RequestMethod.GET)
    public String showExchangeHouseEditFormAdmin(Model model, @PathVariable(required = true, name= "id") String id){
        int idInIntegerFormat = Integer.parseInt(id);
        ExchangeHouseModel exchangeHouseModelSelected = exchangeHouseModelService.getExchangeHouseByExchangeId(idInIntegerFormat);
        model.addAttribute("exchangeHouse", exchangeHouseModelSelected);
        return "pages/admin/adminExchangeHouseEditForm";
    }
    @RequestMapping(value="/editExchangeHouse/{id}", method= RequestMethod.POST)
    public String editExchangeHouse(Model model, @PathVariable(required = true, name= "id") String id, @Valid ExchangeHouseModel exchangeHouseModel, BindingResult result, RedirectAttributes ra){
        int idInIntegerFormat = Integer.parseInt(id);
        if (result.hasErrors()) {
            exchangeHouseModel.setId(idInIntegerFormat);
            ra.addFlashAttribute("message","Operation Unsuccessfull!");
            return "editExchangeHouse";
        }
        try {
            exchangeHouseModelService.editExchangeHouse(exchangeHouseModel);
            ra.addFlashAttribute("message","Exchange House Has been Edited Successfully");
            model.addAttribute("exchangeHouse",exchangeHouseModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/adminReport?type=8";
    }

    @GetMapping(value="/getExchangeHouse", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExchangeHouse(@RequestParam(defaultValue = "") String exchangeCode, @RequestParam(defaultValue = "") String nrtaCode){
        Map<String, Object> resp = exchangeHouseModelService.getExchangeHouse(exchangeCode, nrtaCode);
        return ResponseEntity.ok(resp);
    }
}
