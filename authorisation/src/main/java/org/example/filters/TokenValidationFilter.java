package org.example.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.utils.TokenHandler;
import org.example.utils.data.Roles;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TokenValidationFilter implements Filter {
    private final List<String> excludedUrls;

    public TokenValidationFilter() {
        this.excludedUrls = List.of("/api/companies.*", "/api/users/login");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, List<String>> headersMap = getHeadersFromServletRequest(servletRequest);

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if (httpServletRequest.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if(shouldBeExcluded(httpServletRequest.getRequestURI())){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            List<String> authHeader = headersMap.get("authorization");

            if (authHeader == null) {
                throw new JWTVerificationException("No authentication header present");
            }

            String token = authHeader.get(0);

            DecodedJWT decodedJWT = TokenHandler.validateToken(token);
            servletRequest.setAttribute("user", decodedJWT.getClaim("sub").asString());
            servletRequest.setAttribute("company",
                    decodedJWT.getClaim("company").asString().equals("null") ? null : UUID.fromString(decodedJWT.getClaim("company").asString()));
            servletRequest.setAttribute("roles", decodedJWT.getClaim("roles").asList(Roles.class));

        } catch (JWTVerificationException e) {
            ((HttpServletResponse) servletResponse).sendError(HttpStatus.UNAUTHORIZED.value());
            return;

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Map<String, List<String>> getHeadersFromServletRequest(ServletRequest servletRequest) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        return Collections.list(req.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(req.getHeaders(h))
                ));
    }

    private boolean shouldBeExcluded(String url){
        for (String pattern : this.excludedUrls) {
            if (url.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}
