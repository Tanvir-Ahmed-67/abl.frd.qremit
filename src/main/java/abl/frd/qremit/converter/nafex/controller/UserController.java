package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import abl.frd.qremit.converter.nafex.service.NafexModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Controller
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    private final NafexModelService nafexModelService;

    @Autowired
    public UserController(MyUserDetailsService myUserDetailsService, NafexModelService nafexModelService) {
        this.myUserDetailsService = myUserDetailsService;
        this.nafexModelService = nafexModelService;
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
    public String showUserCreateFromSuperAdmin(Model model){
        model.addAttribute("user", new User());
        return "/pages/superAdmin/superAdminNewUserEntryPage";
    }
    @RequestMapping(value = "/createNewUser", method = RequestMethod.POST)
    public String submitUserCreateFromSuperAdmin(User user, RedirectAttributes ra){
        user.setStatus(true);
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
}
