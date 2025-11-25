package com.company.web.springdemo.controllers.mvc;

import com.company.web.springdemo.exceptions.AuthorizationException;
import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.helpers.AuthenticationHelper;
import com.company.web.springdemo.models.LoginDto;
import com.company.web.springdemo.models.RegisterDto;
import com.company.web.springdemo.models.User;
import com.company.web.springdemo.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AuthenticationMvcController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isLoggedIn")
    public boolean populateIsLoggedIn(HttpSession session) {
        return authenticationHelper.isLoggedIn(session);
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        if (authenticationHelper.isLoggedIn(session)) {
            return "redirect:/";
        }
        model.addAttribute("loginDto", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }

        try {
            User user = authenticationHelper.authenticate(loginDto.getUsername(), loginDto.getPassword());
            session.setAttribute("currentUser", user.getUsername());
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (AuthorizationException ex) {
            bindingResult.rejectValue("username", "auth_error", ex.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpSession session) {
        if (authenticationHelper.isLoggedIn(session)) {
            return "redirect:/";
        }
        model.addAttribute("registerDto", new RegisterDto());
        return "RegisterView";
    }

    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "RegisterView";
        }

        try {
            User user = userService.create(
                    registerDto.getUsername(),
                    registerDto.getPassword(),
                    registerDto.getFirstName(),
                    registerDto.getLastName(),
                    registerDto.getEmail()
            );
            session.setAttribute("currentUser", user.getUsername());
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (EntityDuplicateException ex) {
            bindingResult.rejectValue("username", "user.duplicate", ex.getMessage());
            return "RegisterView";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @ExceptionHandler(AuthorizationException.class)
    public String handleAuthorizationException(AuthorizationException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "NotFoundView";
    }
}