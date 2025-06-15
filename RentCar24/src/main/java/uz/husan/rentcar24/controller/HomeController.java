package uz.husan.rentcar24.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
    @GetMapping("/aloqa")
    public String aloqa(){
        return "aloqa";
    }

}
