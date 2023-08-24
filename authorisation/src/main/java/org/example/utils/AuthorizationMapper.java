package org.example.utils;

import org.example.utils.data.JwtClaims;
import org.example.utils.data.Roles;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AuthorizationMapper {
    @SuppressWarnings("unchecked cast")
    public static JwtClaims servletRequestToJWTClaims(HttpServletRequest httpServletRequest){
        return JwtClaims.builder()
                .username((String) httpServletRequest.getAttribute("username"))
                .companyUUID((UUID) httpServletRequest.getAttribute("company"))
                .roles(new HashSet<>((List<Roles>) httpServletRequest.getAttribute("roles")))
                .build();
    }
}
