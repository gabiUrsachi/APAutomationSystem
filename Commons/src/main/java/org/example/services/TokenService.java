package org.example.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.Roles;
import org.example.errorhandling.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final Algorithm ALGORITHM = Algorithm.HMAC256("APAutomation");
    private final String ISSUER = "APSystem";



    private Set<String> blacklist;

    public String createToken(String username, Set<Roles> roles){

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer("APSystem")
                .withSubject(username)
                .withClaim("roles", roles.stream().map(Enum::toString).collect(Collectors.toList()))
                .withExpiresAt(computeExpDate())
                .sign(ALGORITHM);
    }

    public String validateToken(String token){
        String jwt = token.split(" ")[1];

        JWTVerifier verifier = JWT
                .require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();

            DecodedJWT decodedJWT = verifier.verify(jwt);

            if(blacklist.contains(decodedJWT.getClaim("jti").asString())){
                throw new InvalidTokenException();
            }

            return decodedJWT.getClaim("sub").asString();

    }

    public void invalidateToken(String JWTId){
        blacklist.add(JWTId);
    }

    private Date computeExpDate(){
        return new Date(System.currentTimeMillis() + 10000L);
    }
}
