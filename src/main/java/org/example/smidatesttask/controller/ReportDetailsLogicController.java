package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.service.ReportDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * controller for companies logic
 */
@Controller
@RequestMapping("/api/v1/reports_details")
@RequiredArgsConstructor
public class ReportDetailsLogicController {

    private final ReportDetailsService reportDetailsService;

    /**
     * submit report details function
     *
     * @param reportDetailsDTO - report details data from user
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
    @PostMapping("/submit_report_details")
    public String submitReportDetails(@ModelAttribute("reportDetails") ReportDetailsDTO reportDetailsDTO, RedirectAttributes redirectAttributes) {
        try {
            reportDetailsService.createOrUpdateReportDetails(reportDetailsDTO);

            return "redirect:/api/v1/reports_details/" + reportDetailsDTO.getReportId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (reportDetailsService.isReportDetailsExistDb(reportDetailsDTO.getReportId())) {
            return "redirect:/api/v1/reports_details/update/" + reportDetailsDTO.getReportId();
        } else {
            return "redirect:/api/v1/reports_details/create/" + reportDetailsDTO.getReportId();
        }
    }

    /**
     * delete report details function
     *
     * @param reportDetailsId - selected report details id
     * @param redirectAttributes - attributes to redirect
     * @return a page
     */
    @GetMapping("/delete/{reportDetailsId}")
    public String deleteReportDetailsId(@PathVariable("reportDetailsId") UUID reportDetailsId, RedirectAttributes redirectAttributes) {
        try {
            reportDetailsService.delete(reportDetailsId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            if (e.getMessage().equals("Report details with this id not found")) {
                return "redirect:/api/v1/companies";
            }
        }

        return "redirect:/api/v1/report_details/" + reportDetailsId;
    }
}
