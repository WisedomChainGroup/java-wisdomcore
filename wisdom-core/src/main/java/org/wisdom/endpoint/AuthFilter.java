package org.wisdom.endpoint;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.wisdom.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends BasicAuthenticationFilter {
    public AuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String contextPath = "wisdom/shutdown";
        if (!request.getRequestURI().contains(contextPath)) {
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("token");
        if (token != null && !token.equals("")) {
            if (JWTUtil.parseJWT(token)) {
                chain.doFilter(request, response);
                return;
            }
        }
        response.sendError(401, "jwt token err");
    }
}
