package com.mes.mes.controller;

import com.mes.mes.entity.User;
import com.mes.mes.service.AuthService;
import com.mes.mes.support.SessionAuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute(SessionAuthUtil.ATTR_USER_ID) != null && model.getAttribute("error") == null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = authService.authenticate(username, password);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/login";
        }
        User user = userOpt.get();
        session.setAttribute(SessionAuthUtil.ATTR_USER_ID, user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute(SessionAuthUtil.ATTR_ROLE, user.getRole().name());
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
