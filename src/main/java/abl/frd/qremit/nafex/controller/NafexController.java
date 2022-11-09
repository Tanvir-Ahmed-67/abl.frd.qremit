package abl.frd.qremit.nafex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qremit")
public class NafexController {
    @GetMapping(value = "/")
    public String homePage() {
        System.out.println("inside controller");
        return "index";
    }

}
