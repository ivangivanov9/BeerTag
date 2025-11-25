package com.company.web.springdemo.controllers.mvc;

import com.company.web.springdemo.helpers.AuthenticationHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public HomeMvcController(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isLoggedIn")
    public boolean populateIsLoggedIn(HttpSession session) {
        return authenticationHelper.isLoggedIn(session);
    }

    @GetMapping
    public String showHomePage(Model model, HttpSession session) {
        boolean isLoggedIn = authenticationHelper.isLoggedIn(session);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "HomeView";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, HttpSession session) {
        boolean isLoggedIn = authenticationHelper.isLoggedIn(session);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "About";
    }
}