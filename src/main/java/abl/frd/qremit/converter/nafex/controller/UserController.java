package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.NafexModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Controller
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    private final NafexModelService nafexModelService;
    private final ExchangeHouseModelService exchangeHouseModelService;

    @Autowired
    public UserController(MyUserDetailsService myUserDetailsService, 
    NafexModelService nafexModelService,
     ExchangeHouseModelService exchangeHouseModelService) {

        this.myUserDetailsService = myUserDetailsService;
        this.nafexModelService = nafexModelService;
        this.exchangeHouseModelService = exchangeHouseModelService;
    }
    @RequestMapping("/login")
    public String loginPage(){
        return "auth-login";
    }
    @RequestMapping("/super-admin-home-page")
    public String loginSubmitSuperAdmin(){ return "/layouts/dashboard"; }
    @RequestMapping("/admin-home-page")
    public String loginSubmitAdmin(){ return "/layouts/dashboard"; }
    @RequestMapping("/user-home-page")
    public String loginSubmitUser(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        String nrtaCode = userDetails.getUserNrtaCode();
        String[] arrOfNrtaCode = nrtaCode.split(",");
        model.addAttribute("arrOfNrtaCode", arrOfNrtaCode);
        return "/layouts/dashboard"; }
    @RequestMapping("/home")
    public String loginSubmit(){
        return "/layouts/dashboard";
    }
    @RequestMapping("/logout")
    public String logoutSuccessPage(){
        return "auth-login";
    }
    @RequestMapping("/allUsers")
    public String loadAllUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<User> userList;
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN")) {
                userList = myUserDetailsService.loadAllUser();
                model.addAttribute("UserList", userList);
                return "/pages/superAdmin/superAdminUserListPage";
            }
            if (authorityName.equals("ROLE_ADMIN")) {
                userList = myUserDetailsService.loadUsersOnly();
                model.addAttribute("UserList", userList);
                return "/pages/admin/adminUserListPage";
            }
        }
        return "/allUsers";
    }
    @RequestMapping("/newUserCreationForm")
    public String showUserCreateFromAdmin(Model model){
        model.addAttribute("user", new User());

        List<ExchangeHouseModel> exchangeHouseList;
        exchangeHouseList = exchangeHouseModelService.loadAllExchangeHouse();
        model.addAttribute("exchangeList", exchangeHouseList);

        return "/pages/admin/adminNewUserEntryForm";
    }

    @RequestMapping("/newExchangeHouseList")
    public  String generateExchangeHouseLisString(Model model){

       List<ExchangeHouseModel> exchangeHouseList;
        exchangeHouseList = exchangeHouseModelService.loadAllExchangeHouse();
        model.addAttribute("exchangeList", exchangeHouseList);

        return "/pages/admin/adminNewUserEntryForm";
    }  


    @RequestMapping(value = "/createNewUser", method = RequestMethod.POST)
    public String submitUserCreateFromSuperAdmin(User user, RedirectAttributes ra){
        user.setActiveStatus(false);
        myUserDetailsService.insertUser(user);
        ra.addFlashAttribute("message","New User has been created successfully");
        return "redirect:/allUsers";
    }
    @GetMapping("/adminDashboard")
    @ResponseBody
    public List<Integer> loadAdminDashboard(Model model){
        List<Integer> count = nafexModelService.CountAllFourTypesOfData();
        return count;
    }
    @RequestMapping(value="/userEditForm/{id}", method = RequestMethod.POST)
    public String showUserEditForm(Model model, @PathVariable(required = true, name= "id") String id, @Valid User user){
        model.addAttribute("user", user);
        return "/pages/admin/adminUserEditForm";
    }
    @RequestMapping(value="/editUser/{id}", method= RequestMethod.POST)
    public String editExchangeHouse(Model model, @PathVariable(required = true, name= "id") String id, @Valid User user, BindingResult result, RedirectAttributes ra){
        int idInIntegerFormat = Integer.parseInt(id);
        if (result.hasErrors()) {
            user.setId(idInIntegerFormat);
            return "editUser";
        }
        try {
            myUserDetailsService.editUser(user);
            ra.addFlashAttribute("message","User Updated successfully");
            model.addAttribute("user",user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/allUsers";
    }
}
