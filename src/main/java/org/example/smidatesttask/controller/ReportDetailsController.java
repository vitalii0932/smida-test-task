package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.service.ReportDetailsService;
import org.example.smidatesttask.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * handles the GET request for the report details page
     *
     * @param model    - the model object used to pass data to the view
     * @param reportId - report id
     * @return the view name for the report details page
     */
    @GetMapping("/{reportId}")
    public String loadReportDetailsPage(Model model, @PathVariable("reportId") UUID reportId) {
        Report selectedReport = reportService.findReportById(reportId);
        ReportDetails reportDetails;
        try {
            reportDetails = reportDetailsService.getReportDetails(reportId);
        } catch (Exception e) {
            reportDetails = null;
        }
        model.addAttribute("company", selectedReport.getCompany());
        model.addAttribute("report", selectedReport);
        model.addAttribute("reportDetails", reportDetails);
        return "reports_details";
    }

    /**
     * handles the GET request for the create report details page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the create report details page
     */
    @GetMapping("/create/{reportId}")
    public String loadCreateReportDetailsPage(Model model, @PathVariable("reportId") UUID reportId) {
        try {
            reportDetailsService.getReportDetails(reportId);
        } catch (Exception e) {
            ReportDetailsDTO reportDetailsDTO = new ReportDetailsDTO();
            reportDetailsDTO.setReportId(reportId);

            model.addAttribute("reportDetails", reportDetailsDTO);

            return "create_or_update_report_details";
        }
        return "redirect:/api/v1/reports_details/update/" + reportId;
    }

    /**
     * handles the GET request for the update report details page
     *
     * @param model - the model object used to pass data to the view
     * @return the view name for the update report details page
     */
    @GetMapping("/update/{reportId}")
    public String loadUpdateReportDetailsPage(Model model, @PathVariable("reportId") UUID reportId) {
        model.addAttribute("reportDetails", reportDetailsService.getReportDetailsDTO(reportId));
        return "create_or_update_report_details";
    }
}
