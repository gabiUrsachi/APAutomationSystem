package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.Roles;
import org.example.customexceptions.InvalidTokenException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenHandler {
    private final static Algorithm ALGORITHM = Algorithm.HMAC256("APAutomation");
    private final static String ISSUER = "APSystem";

    private static Set<String> blacklist = new HashSet<>();

    public static String createToken(String username, UUID companyUUID, Set<Roles> roles) {

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer("APSystem")
                .withSubject(username)
                .withClaim("company", companyUUID.toString())
                .withClaim("roles", roles.stream().map(Enum::toString).collect(Collectors.toList()))
                .withExpiresAt(computeExpDate())
                .sign(ALGORITHM);
    }

    public static DecodedJWT validateToken(String token){
        String jwt = token.split(" ")[1];

        JWTVerifier verifier = JWT
                .require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();

        DecodedJWT decodedJWT = verifier.verify(jwt);

        if (blacklist.contains(decodedJWT.getClaim("jti").asString())) {
            throw new InvalidTokenException();
        }

        return decodedJWT;
    }

    public static void invalidateToken(DecodedJWT decodedJWT) {
        String jti = decodedJWT.getClaim("jti").asString();

        blacklist.add(jti);
    }

    public static Set<Roles> getRolesFromToken(DecodedJWT decodedJWT ) {
        return new HashSet<>(decodedJWT.getClaim("roles").asList(Roles.class));
    }

    public static String getSubjectFromToken(DecodedJWT decodedJWT ) {
        return decodedJWT.getClaim("sub").asString();
    }

    public static UUID getCompanyFromToken(DecodedJWT decodedJWT ) {
        return UUID.fromString(decodedJWT.getClaim("company").asString());
    }

    private static Date computeExpDate() {
        return new Date(System.currentTimeMillis() + 600000L);
    }
}
