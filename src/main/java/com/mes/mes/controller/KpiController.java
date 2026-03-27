package com.mes.mes.controller;

import com.mes.mes.entity.UserRole;
import com.mes.mes.service.KpiService;
import com.mes.mes.support.SessionAuthUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class KpiController {

    private final KpiService kpiService;
    private final ObjectMapper objectMapper;

    public KpiController(KpiService kpiService, ObjectMapper objectMapper) {
        this.kpiService = kpiService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/kpi")
    public String kpi(
            HttpSession session,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR, UserRole.QC);
        if (auth != null) {
            return auth;
        }

        var kpi = kpiService.buildKpi(startDate, endDate);
        model.addAttribute("kpi", kpi);

        try {
            Map<String, Object> bar = new LinkedHashMap<>();
            bar.put("labels", kpi.getProcessChartLabels());
            bar.put("data", kpi.getProcessChartYields().stream()
                    .map(BigDecimal::doubleValue)
                    .collect(Collectors.toList()));
            model.addAttribute("barChartJson", objectMapper.writeValueAsString(bar));

            Map<String, Object> pie = new LinkedHashMap<>();
            pie.put("labels", kpi.getDefectChartLabels());
            List<Long> qtys = kpi.getDefectChartQtys();
            pie.put("data", qtys.stream().map(Long::doubleValue).collect(Collectors.toList()));
            model.addAttribute("pieChartJson", objectMapper.writeValueAsString(pie));
        } catch (JsonProcessingException e) {
            model.addAttribute("barChartJson", "{\"labels\":[],\"data\":[]}");
            model.addAttribute("pieChartJson", "{\"labels\":[],\"data\":[]}");
        }

        return "kpi";
    }
}
