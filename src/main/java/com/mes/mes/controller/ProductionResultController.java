package com.mes.mes.controller;

import com.mes.mes.entity.Lot;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.UserRole;
import com.mes.mes.service.ProductionResultService;
import com.mes.mes.support.SessionAuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class ProductionResultController {

    private final ProductionResultService productionResultService;

    public ProductionResultController(ProductionResultService productionResultService) {
        this.productionResultService = productionResultService;
    }

    @GetMapping("/results/new")
    public String newForm(
            HttpSession session,
            @RequestParam Long lotId,
            Model model,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR);
        if (auth != null) {
            return auth;
        }

        Lot lot = productionResultService.getLotForResultForm(lotId);
        if (lot == null) {
            redirectAttributes.addFlashAttribute("error", "LOT을 찾을 수 없습니다.");
            return "redirect:/lots";
        }
        if (lot.getStatus() != LotStatus.IN_PROGRESS) {
            redirectAttributes.addFlashAttribute("error", "실적 입력은 진행 중(IN_PROGRESS) LOT만 가능합니다.");
            return "redirect:/lots";
        }

        model.addAttribute("lot", lot);
        model.addAttribute("plannedQty", lot.getWorkOrder().getPlannedQty());
        return "result-form";
    }

    @PostMapping("/results")
    public String save(
            HttpSession session,
            @RequestParam Long lotId,
            @RequestParam Integer goodQty,
            @RequestParam Integer defectQty,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            RedirectAttributes redirectAttributes) {
        String auth = SessionAuthUtil.redirectIfNotRole(session, redirectAttributes, UserRole.OPERATOR);
        if (auth != null) {
            return auth;
        }
        Long userId = (Long) session.getAttribute(SessionAuthUtil.ATTR_USER_ID);
        LocalDateTime startParsed;
        LocalDateTime endParsed;
        try {
            startParsed = parseOptionalDateTime(startTime);
            endParsed = parseOptionalDateTime(endTime);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/results/new?lotId=" + lotId;
        }
        try {
            productionResultService.saveResult(lotId, userId, goodQty, defectQty, startParsed, endParsed);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/results/new?lotId=" + lotId;
        }
        redirectAttributes.addFlashAttribute("message", "실적이 저장되었고 LOT이 완료되었습니다.");
        return "redirect:/lots";
    }

    /**
     * 빈 값은 null(서비스에서 현재 시각으로 대체). datetime-local 및 ISO 변형 파싱.
     */
    private static LocalDateTime parseOptionalDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String s = raw.trim();
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        throw new IllegalArgumentException("시작·종료 시간 형식이 올바르지 않습니다. 비우면 저장 시각으로 자동 입력됩니다.");
    }
}
