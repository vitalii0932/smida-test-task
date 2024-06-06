package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.mapper.CompanyMapper;
import org.example.smidatesttask.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Controller for companies page
 */
@Controller
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesPageController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

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

    /**
     * handles the GET request for the create company page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the create company page
     */
    @GetMapping("/create")
    public String loadCreateCompanyPage(Model model) {
        CompanyDTO companyDTO = new CompanyDTO();
        model.addAttribute("company", companyDTO);
        return "create_or_update_company";
    }

    /**
     * handles the GET request for the update company page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the update company page
     */
    @GetMapping("/update/{companyId}")
    public String loadUpdateCompanyPage(Model model, @PathVariable("companyId") UUID companyId) {
        model.addAttribute("company", companyService.findCompanyDTOById(companyId));
        return "create_or_update_company";
    }
}
