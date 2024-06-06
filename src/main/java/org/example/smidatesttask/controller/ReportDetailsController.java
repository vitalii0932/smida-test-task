package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.service.ReportDetailsService;
import org.example.smidatesttask.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * Controller for report details page
 */
@Controller
@RequestMapping("/api/v1/reports_details")
@RequiredArgsConstructor
public class ReportDetailsController {

    private final ReportDetailsService reportDetailsService;
    private final ReportService reportService;

    @GetMapping("/{reportId}")
    public String loadReportDetailsPage(Model model, @PathVariable("reportId") UUID reportId) {
        Report selectedReport = reportService.findReportById(reportId);
        ReportDetails reportDetails = reportDetailsService.getReportDetails(reportId);
        model.addAttribute("company", selectedReport.getCompany());
        model.addAttribute("report", selectedReport);
        model.addAttribute("reportDetails", reportDetails);
        return "reports_details";
    }
}
