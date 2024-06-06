package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesLogicController {

    private final CompanyService companyService;

    @PostMapping("/submit_company")
    public String submitCompany(@ModelAttribute("company") CompanyDTO companyDTO, RedirectAttributes redirectAttributes) {
        try {
            companyService.createOrUpdateCompany(companyDTO);

            return "redirect:/api/v1/companies";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getViolations());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (companyDTO.getId() == null) {
            return "redirect:/api/v1/companies/create";
        } else {
            return "redirect:/api/v1/companies/update";
        }
    }
}
