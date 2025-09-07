package com.example.calendar_h.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login/index";
    }
    
    @GetMapping("/signup")
    public String signUp() {
        return "login/signup";
    }
}
