package org.example.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.AuthorisationControllerAdvice;
import org.example.errorhandling.utils.ExceptionResponseDTO;
import org.example.errorhandling.customexceptions.InvalidTokenException;
import org.example.utils.TokenHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TokenValidationFilter implements Filter {
    private AuthorisationControllerAdvice controllerAdvice;

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(arg0.getServletContext());

        this.controllerAdvice = ctx.getBean(AuthorisationControllerAdvice.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        Map<String, List<String>> headersMap = Collections.list(req.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(req.getHeaders(h))
                ));

        try {
            List<String> authHeader = headersMap.get("authorization");
            if (authHeader == null ){
                throw new JWTVerificationException("No authentication header present");
            }
            String token = authHeader.get(0);

            DecodedJWT decodedJWT = TokenHandler.validateToken(token);
            servletRequest.setAttribute("user", TokenHandler.getSubjectFromToken(decodedJWT));
            servletRequest.setAttribute("company", TokenHandler.getCompanyFromToken(decodedJWT));
            servletRequest.setAttribute("roles", TokenHandler.getRolesFromToken(decodedJWT));

        } catch (InvalidTokenException | JWTVerificationException e) {

            ResponseEntity<ExceptionResponseDTO> exceptionResponse = this.controllerAdvice.handleInvalidTokenException(e);
            ((HttpServletResponse) servletResponse).sendError(exceptionResponse.getStatusCodeValue());

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
