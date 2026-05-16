package com.sil.club.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.sil.club.entity.User;
import com.sil.club.exception.AuthException;
import com.sil.club.service.IUserService;

public final class AuthUtil {

    public static final String SESSION_USER_ID = "LOGIN_USER_ID";

    private AuthUtil() {
    }

    public static User requireCurrentUser(HttpServletRequest request, IUserService userService) {
        Long userId = getSessionUserId(request);
        if (userId == null) {
            throw new AuthException(401, "请先登录");
        }

        User user = userService.getById(userId);
        if (user == null) {
            throw new AuthException(401, "登录信息已失效，请重新登录");
        }
        return user;
    }

    public static void storeSessionUserId(HttpSession session, Long userId) {
        if (session != null && userId != null) {
            session.setAttribute(SESSION_USER_ID, userId);
        }
    }

    public static void clearSessionUserId(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_ID);
        }
    }

    private static Long getSessionUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_USER_ID);
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value != null) {
            try {
                return Long.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static void requireAdmin(HttpServletRequest request, IUserService userService) {
        User user = requireCurrentUser(request, userService);
        boolean isAdmin = Integer.valueOf(0).equals(user.getGlobalRole())
                || "ROLE_ADMIN".equalsIgnoreCase(user.getRole());
        if (!isAdmin) {
            throw new AuthException(403, "仅管理员可操作全局活动");
        }
    }
}
