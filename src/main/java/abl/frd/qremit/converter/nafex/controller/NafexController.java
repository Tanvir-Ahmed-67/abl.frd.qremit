package abl.frd.qremit.converter.nafex.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qremit")
public class NafexController {
    @GetMapping(value = "/index")
    public String homePage() {
        System.out.println("inside controller");
        return "nafex";
    }

    @GetMapping(value = "/error")
    public String errorPage() {
        System.out.println("inside controller error");
        return "nafex";
    }

}
