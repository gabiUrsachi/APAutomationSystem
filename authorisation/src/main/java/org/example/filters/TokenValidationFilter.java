package org.example.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.AuthorisationControllerAdvice;
import org.example.utils.data.Roles;
import org.example.customexceptions.InvalidTokenException;
import org.example.utils.ExceptionResponseDTO;
import org.example.utils.TokenHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

@Component
public class TokenValidationFilter implements Filter {

    private final AuthorisationControllerAdvice controllerAdvice;

    public TokenValidationFilter(AuthorisationControllerAdvice controllerAdvice) {
        this.controllerAdvice = controllerAdvice;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, List<String>> headersMap = getHeadersFromServletRequest(servletRequest);

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

        } catch (InvalidTokenException | JWTVerificationException e) {
            ResponseEntity<ExceptionResponseDTO> exceptionResponse = this.controllerAdvice.handleInvalidTokenException(e);

            ((HttpServletResponse) servletResponse).sendError(exceptionResponse.getStatusCodeValue());
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
}
