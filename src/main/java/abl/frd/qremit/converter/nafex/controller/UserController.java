package abl.frd.qremit.converter.nafex.controller;

import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
        List<User> userList = myUserDetailsService.loadAllUser();
        model.addAttribute("UserList", userList);
        return "/pages/userDetailPage";
    }
}
