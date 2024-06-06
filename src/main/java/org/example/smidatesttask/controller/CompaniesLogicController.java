package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.service.CompanyService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * controller for companies logic
 */
@Controller
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesLogicController {

    private final CompanyService companyService;

    /**
     * submit company function
     *
     * @param companyDTO - company data from user
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
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

    /**
     * delete category function
     *
     * @param companyId - company id
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
    @GetMapping("/delete/{companyId}")
    public String deleteCategory(@PathVariable("companyId") UUID companyId, RedirectAttributes redirectAttributes) {
        try {
            companyService.delete(companyId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/api/v1/companies";
    }
}
