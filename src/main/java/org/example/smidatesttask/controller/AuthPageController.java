package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.AuthenticationRequest;
import org.example.smidatesttask.dto.AuthenticationResponse;
import org.example.smidatesttask.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for auth pages
 */
@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthPageController {

    /**
     * handles the GET request for the login page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the login page
     */
    @GetMapping("/login_page")
    public String loadLoginPage(Model model) {
        model.addAttribute("user", new AuthenticationRequest());
        return "login";
    }

    /**
     * handles the GET request for the registration page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the registration page
     */
    @GetMapping("/register_page")
    public String loadRegistrationPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "registration";
    }
}
