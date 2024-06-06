package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for companies page
 */
@Controller
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesPageController {

    private final CompanyService companyService;

    /**
     * handles the GET request for the companies page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the companies page
     */
    @GetMapping
    public String loadCompaniesPage(Model model) {
        model.addAttribute("companies", companyService.getAll());
        return "companies";
    }
}
