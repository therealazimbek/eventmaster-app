package com.therealazimbek.spring.eventmasterapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public String welcome() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
