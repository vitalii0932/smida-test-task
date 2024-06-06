package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.service.ReportDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * controller for companies logic
 */
@Controller
@RequestMapping("/api/v1/reports_details")
@RequiredArgsConstructor
public class ReportDetailsLogicController {

    private final ReportDetailsService reportDetailsService;
}
