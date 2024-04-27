package com.van1164.webfluxsecurityexample.user_info;

import com.nimbusds.oauth2.sdk.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final long EXPIRATION_MILLISECONDS = 10000 * 60 * 30;
    private SecretKey key;


    @Value("${jwt.secret}")
    public void setKey(String key) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));;
    }
    public TokenInfo createToken(String email) {
        Claims claims = Jwts.claims();
        claims.setSubject(email);
        claims.put("roles", Role.values());

        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + EXPIRATION_MILLISECONDS);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new TokenInfo("Bearer", accessToken);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Object auth = claims.get("auth");
        if (auth == null) {
            throw new RuntimeException("Invalid Token");
        }
        String email = ((LinkedHashMap<?, ?>) auth).get("sub").toString();
        Collection<GrantedAuthority> authorities = Arrays.stream((auth.toString())
                .split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());


        UserDetails principal = new User(email, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

