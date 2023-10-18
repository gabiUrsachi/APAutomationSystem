package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.utils.data.Roles;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The TokenHandler class provides methods for creating, validating, and managing JWT tokens.
 */
public class TokenHandler {
    private final static Algorithm ALGORITHM = Algorithm.HMAC256("APAutomation");
    private final static String ISSUER = "APSystem";
    private final static Long TIMEOUT = 600000L;


    /**
     * Creates a JWT token containing user information, roles, and expiration.
     *
     * @param username    the username associated with the token
     * @param companyUUID the UUID of the company associated with the token
     * @param roles       the set of roles associated with the user
     * @return the generated JWT token
     */
    public static String createToken(String username, UUID companyUUID, Set<Roles> roles) {

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer("APSystem")
                .withSubject(username)
                .withClaim("company", companyUUID != null ? companyUUID.toString() : "null")
                .withClaim("roles", roles.stream().map(Enum::toString).collect(Collectors.toList()))
                .withExpiresAt(computeExpDate())
                .sign(ALGORITHM);
    }

    /**
     * Validates a JWT token and returns the decoded JWT object
     *
     * @param token the token string to validate
     * @return the decoded JWT object.
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or blacklisted
     */
    public static DecodedJWT validateToken(String token) {
        String jwt = token.split(" ")[1];

        JWTVerifier verifier = JWT
                .require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(jwt);
    }

    private static Date computeExpDate() {
        return new Date(System.currentTimeMillis() + TIMEOUT);
    }
}
