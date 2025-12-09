package abl.frd.qremit.converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
import abl.frd.qremit.converter.service.SpotCashService;

@Controller
@RequestMapping("/spotcash/")
public class SpotCashController {
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    ExchangeHouseModelService exchangeHouseModelService;
    @Autowired
    CommonService commonService;
    @Autowired
    SpotCashService spotCashService;
    @GetMapping("/index")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model, @RequestParam String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails)); 
        if(authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/login";
        }
        List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelService.getSpotCashExchangeList();
        model.addAttribute("exchangeHouseList", exchangeHouseModelList);
        return "pages/user/spot_cash_upload";
    }
    @PostMapping(value="/upload", produces = "application/json")
    @ResponseBody
    public Map<String, Object> save(@AuthenticationPrincipal MyUserDetails userDetails,@RequestParam("file") MultipartFile file,Model model,
        @RequestParam("exchangeCode") String exchangeCode){
        Map<String, Object> resp = new HashMap<>();
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        int userId = 0;
        // Getting Logged In user Details in this block
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
            User user = myUserDetails.getUser();
            userId = user.getId();
        }
        ExchangeHouseModel exchangeHouseModel = exchangeHouseModelService.findByExchangeCode(exchangeCode);
        if (CommonService.hasCSVFormat(file)) {
            if(!commonService.ifFileExist(file.getOriginalFilename())){
                try{
                    return spotCashService.save(file, userId, exchangeCode, exchangeHouseModel.getNrtaCode());
                }catch(Exception e){
                    e.printStackTrace();
                    return CommonService.getResp(1, e.getMessage(), null);
                }
            }
            String msg = "File With The Name "+ file.getOriginalFilename() +" Already Exists !!";
            return CommonService.getResp(1, msg, null);
        }
        return resp;
    }
}
