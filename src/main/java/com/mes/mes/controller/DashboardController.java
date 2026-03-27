package com.mes.mes.controller;

import com.mes.mes.service.DashboardService;
import com.mes.mes.support.SessionAuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotLoggedIn(session);
        if (auth != null) {
            return auth;
        }
        model.addAttribute("dashboard", dashboardService.load());
        return "dashboard";
    }
}
