package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Controller
public class ExchangeHouseModelController {
    @Autowired
    private final ExchangeHouseModelService exchangeHouseModelService;

    public ExchangeHouseModelController(ExchangeHouseModelService exchangeHouseModelService){
        this.exchangeHouseModelService = exchangeHouseModelService;
    }
    @RequestMapping("/viewAllExchangeHouse")
    public String loadAllExchangeHouse(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<ExchangeHouseModel> exchangeHouseModelList;
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN")) {
                exchangeHouseModelList = exchangeHouseModelService.loadAllExchangeHouse();
                model.addAttribute("exchangeHouseList", exchangeHouseModelList);
                return "/pages/superAdmin/superAdminExchangeHouseListPage";
            }
            if (authorityName.equals("ROLE_ADMIN")) {
                exchangeHouseModelList = exchangeHouseModelService.loadAllExchangeHouse();
                model.addAttribute("exchangeHouseList", exchangeHouseModelList);
                return "/pages/admin/adminExchangeHouseListPage";
            }
        }
        return "/viewAllExchangeHouse";
    }
    @RequestMapping("/newExchangeHouseCreationForm")
    public String showNewExchangeHouseCreateFromAdmin(Model model){
        model.addAttribute("exchangeHouse", new ExchangeHouseModel());
        return "/pages/admin/adminNewExchangeHouseEntryPage";
    }
    @RequestMapping(value = "/createNewExchange", method = RequestMethod.POST)
    public String submitNewExchangeHouseCreateFromAdmin(ExchangeHouseModel exchangeHouseModel, RedirectAttributes ra){
        exchangeHouseModel.setIsActive("0");
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
        return "/pages/superAdmin/superAdminInactiveExchangeHouseListPage";
    }

    @RequestMapping(value="/activateExchangeHouse/{id}", method = RequestMethod.POST)
    public String activateInactiveExchangeHouse(Model model, @PathVariable(required = true, name = "id") String id, RedirectAttributes ra) {
        int idInIntegerFormat = Integer.parseInt(id);
        if(exchangeHouseModelService.updateInactiveExchangeHouse(idInIntegerFormat)){
            ra.addFlashAttribute("message","Exchange House has been activated successfully");
        }
        return "redirect:/showInactiveExchangeHouse";
    }
}
