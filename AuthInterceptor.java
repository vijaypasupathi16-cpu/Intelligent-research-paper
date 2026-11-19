package com.researchintelligence.config;

import com.researchintelligence.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Allow public static assets and public auth pages
        if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images") ||
            path.equals("/") || path.equals("/login") || path.equals("/register")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (user == null) {
            response.sendRedirect("/login?error=session_expired");
            return false;
        }

        // Enforce Admin role for /admin paths
        if (path.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("/dashboard?error=unauthorized");
            return false;
        }

        return true;
    }
}
