package com.mes.mes.controller;

import com.mes.mes.entity.DefectAction;
import com.mes.mes.entity.DefectType;
import com.mes.mes.entity.UserRole;
import com.mes.mes.service.DefectService;
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
public class DefectController {

    private final DefectService defectService;

    public DefectController(DefectService defectService) {
        this.defectService = defectService;
    }

    @GetMapping("/defects")
    public String list(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.QC);
        if (auth != null) {
            return auth;
        }
        model.addAttribute("defects", defectService.findAll());
        model.addAttribute("defectTypes", DefectType.values());
        model.addAttribute("defectActions", DefectAction.values());
        return "defect-list";
    }

    @PostMapping("/defects")
    public String register(
            HttpSession session,
            @RequestParam Long resultId,
            @RequestParam DefectType defectType,
            @RequestParam Integer defectQty,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.QC);
        if (auth != null) {
            return auth;
        }
        try {
            defectService.register(resultId, defectType, defectQty, note);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/defects";
    }

    @PostMapping("/defects/{defectId}/handle")
    public String handle(
            HttpSession session,
            @PathVariable Long defectId,
            @RequestParam DefectType defectType,
            @RequestParam DefectAction action,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.QC);
        if (auth != null) {
            return auth;
        }
        Long userId = (Long) session.getAttribute(SessionAuthUtil.ATTR_USER_ID);
        try {
            defectService.handle(defectId, defectType, action, userId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/defects";
    }
}
