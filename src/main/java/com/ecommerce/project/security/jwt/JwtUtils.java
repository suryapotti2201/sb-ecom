package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;


    public String getJwtFromHeader(HttpServletRequest httpServletRequest){
        log.info("JwtUtils => getJwtFromHeader : In");
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            log.info("JwtUtils => getJwtFromHeader : Out");
            return bearerToken.substring(7);
        }
        log.info("JwtUtils => getJwtFromHeader : Out");
        return null;
    }

    public String generateTokenFromUsername(UserDetails userDetails){
        log.info("JwtUtils => generateTokenFromUsername : In");
        String username = userDetails.getUsername();
        log.info("JwtUtils => generateTokenFromUsername : Out");
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public boolean validateJwtToken(String token){
        log.info("JwtUtils => validateJwtToken : In");
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            log.info("JwtUtils => validateJwtToken : Out");
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid Jwt Token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Jwt token is expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Jwt token is unsupported: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("Jwt clams String is empty: {}", e.getMessage());
        }
        log.info("JwtUtils => validateJwtToken : Out");
        return false;
    }
}
