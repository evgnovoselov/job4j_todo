package ru.job4j.todo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {
    private static final Set<String> URLS_GUEST = Set.of(
            "/users/registration",
            "/users/login",
            "/webjars"
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if ("/".equals(uri) || isAlwaysPermitted(uri)) {
            chain.doFilter(request, response);
            return;
        }
        boolean userLoggedIn = request.getSession().getAttribute("user") != null;
        if (!userLoggedIn) {
            String loginPageUri = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUri);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isAlwaysPermitted(String uri) {
        return URLS_GUEST.stream().anyMatch(uri::startsWith);
    }
}
