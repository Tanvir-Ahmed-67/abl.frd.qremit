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
import org.springframework.web.bind.annotation.RequestMapping;

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
}
