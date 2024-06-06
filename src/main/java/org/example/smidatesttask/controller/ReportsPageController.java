package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * Controller for reports page
 */
@Controller
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsPageController {

    private final ReportService reportService;

    /**
     * handles the GET request for the company reports page
     *
     * @param model - the model object used to pass data to the view
     * @param companyId - company id
     * @return the view name for the company reports page
     */
    @GetMapping("/{companyId}")
    public String loadReportsPage(Model model, @PathVariable("companyId") UUID companyId) {
        List<Report> reports = reportService.getAllReportByCompany(companyId);
        model.addAttribute("companyName", reports.get(0).getCompany().getName());
        model.addAttribute("reports", reports);
        return "reports";
    }
}
