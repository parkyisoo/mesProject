package com.mes.mes.controller;

import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.UserRole;
import com.mes.mes.service.LotService;
import com.mes.mes.support.SessionAuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping("/lots")
    public String list(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR, UserRole.QC);
        if (auth != null) {
            return auth;
        }
        model.addAttribute("lots", lotService.findAll());
        return "lot-list";
    }

    @PostMapping("/lots/{lotId}/status")
    public String changeStatus(
            HttpSession session,
            @PathVariable Long lotId,
            @RequestParam("nextStatus") String nextStatusRaw,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR);
        if (auth != null) {
            return auth;
        }
        LotStatus nextStatus;
        try {
            nextStatus = LotStatus.valueOf(nextStatusRaw);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "잘못된 상태 값입니다.");
            return "redirect:/lots";
        }
        Long userId = (Long) session.getAttribute(SessionAuthUtil.ATTR_USER_ID);
        String error = lotService.changeStatus(lotId, nextStatus, userId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        }
        return "redirect:/lots";
    }
}
