package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * controller for companies logic
 */
@Controller
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsLogicController {
    
    private final ReportService reportService;

    /**
     * submit report function
     *
     * @param reportDTO - report data from user
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
    @PostMapping("/submit_report")
    public String submitReport(@ModelAttribute("report") ReportDTO reportDTO, RedirectAttributes redirectAttributes) {
        try {
            reportService.createOrUpdateReport(reportDTO);

            return "redirect:/api/v1/reports/" + reportDTO.getCompanyId();
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getViolations());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (reportDTO.getId() == null) {
            return "redirect:/api/v1/reports/create/" + reportDTO.getCompanyId();
        } else {
            return "redirect:/api/v1/reports/update/" + reportDTO.getId();
        }
    }

    /**
     * delete category function
     *
     * @param reportId - report id
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
    @GetMapping("/delete/{reportId}")
    public String deleteReport(@PathVariable("reportId") UUID reportId, RedirectAttributes redirectAttributes) {
        try {
            UUID companyId = reportService.findReportDTOById(reportId).getCompanyId();

            reportService.delete(reportId);

            return "redirect:/api/v1/reports/" + companyId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/api/v1/companies";
    }
}
