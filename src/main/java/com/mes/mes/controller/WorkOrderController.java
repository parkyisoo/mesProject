package com.mes.mes.controller;

import com.mes.mes.entity.UserRole;
import com.mes.mes.service.WorkOrderService;
import com.mes.mes.support.SessionAuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/work-orders")
    public String list(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR);
        if (auth != null) {
            return auth;
        }
        model.addAttribute("workOrders", workOrderService.findAll());
        return "work-order-list";
    }

    @GetMapping("/work-orders/new")
    public String newForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.ADMIN);
        if (auth != null) {
            return auth;
        }
        model.addAttribute("products", workOrderService.listProductsForSelect());
        model.addAttribute("processes", workOrderService.listProcessesForSelect());
        return "work-order-form";
    }

    @PostMapping("/work-orders")
    public String create(
            HttpSession session,
            @RequestParam Long productId,
            @RequestParam Long processId,
            @RequestParam Integer plannedQty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedDate,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.ADMIN);
        if (auth != null) {
            return auth;
        }
        Long userId = (Long) session.getAttribute(SessionAuthUtil.ATTR_USER_ID);
        try {
            workOrderService.create(productId, processId, plannedQty, plannedDate, userId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/work-orders/new";
        }
        return "redirect:/work-orders";
    }
}
