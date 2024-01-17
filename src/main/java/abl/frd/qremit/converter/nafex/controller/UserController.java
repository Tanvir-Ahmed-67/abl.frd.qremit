package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.List;

@Controller
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    @Autowired
    public UserController(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
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
                System.out.println(userList.toString());
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
    public String submitUserCreateFromSuperAdmin(User user){
        user.setStatus(true);
        myUserDetailsService.insertUser(user);
        return "redirect:/allUsers";
    }
}
