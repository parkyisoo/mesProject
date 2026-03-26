package com.mes.mes.support;

import com.mes.mes.entity.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 세션 기반 로그인·역할 검사 (Spring Security 미사용).
 */
public final class SessionAuthUtil {

    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_ROLE = "role";
    public static final String MSG_FORBIDDEN = "접근 권한이 없습니다";

    private SessionAuthUtil() {
    }

    public static UserRole parseRole(HttpSession session) {
        Object o = session.getAttribute(ATTR_ROLE);
        if (!(o instanceof String)) {
            return null;
        }
        try {
            return UserRole.valueOf((String) o);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 미로그인 시 {@code redirect:/login}, 아니면 {@code null}.
     */
    public static String redirectIfNotLoggedIn(HttpSession session) {
        if (session.getAttribute(ATTR_USER_ID) == null) {
            return "redirect:/login";
        }
        return null;
    }

    /**
     * ADMIN은 항상 허용. 그 외 {@code allowed} 중 하나일 때만 허용.
     * 거부 시 flash 후 {@code redirect:/login}.
     */
    public static String redirectIfNotRole(HttpSession session, RedirectAttributes ra, UserRole... allowed) {
        String r = redirectIfNotLoggedIn(session);
        if (r != null) {
            return r;
        }
        UserRole role = parseRole(session);
        if (role == null) {
            return "redirect:/login";
        }
        if (role == UserRole.ADMIN) {
            return null;
        }
        for (UserRole a : allowed) {
            if (role == a) {
                return null;
            }
        }
        ra.addFlashAttribute("error", MSG_FORBIDDEN);
        return "redirect:/login";
    }

    public static boolean hasRole(HttpSession session, UserRole... allowed) {
        UserRole role = parseRole(session);
        if (role == null) {
            return false;
        }
        if (role == UserRole.ADMIN) {
            return true;
        }
        for (UserRole a : allowed) {
            if (role == a) {
                return true;
            }
        }
        return false;
    }
}
