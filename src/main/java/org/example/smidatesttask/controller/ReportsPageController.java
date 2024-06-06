package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.mapper.ReportMapper;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.service.CompanyService;
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
    private final CompanyService companyService;
    private final ReportMapper reportMapper;

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
        model.addAttribute("company", reports.get(0).getCompany());
        model.addAttribute("reports", reports);
        return "reports";
    }

    /**
     * handles the GET request for the create report page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the create report page
     */
    @GetMapping("/create/{companyId}")
    public String loadCreateReportPage(Model model, @PathVariable("companyId") UUID companyId) {
        ReportDTO reportDTO = new ReportDTO();
        model.addAttribute("companyId", companyId);
        model.addAttribute("report", reportDTO);
        return "create_or_update_report";
    }

    /**
     * handles the GET request for the update report page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the update report page
     */
    @GetMapping("/update/{reportId}")
    public String loadUpdateReportPage(Model model, @PathVariable("reportId") UUID reportId) {
        UUID companyId = reportService.findReportById(reportId).getCompany().getId();
        ReportDTO reportDTO = reportService.findReportDTOById(reportId);

        model.addAttribute("companyId", companyId);
        model.addAttribute("report", reportDTO);
        return "create_or_update_report";
    }
}
