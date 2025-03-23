package com.example.backend.util;

import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class JWT {
    public final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "ddb1d47e1089c2131d50bf2d74b43b50630228f5e642d74a1d57e84f17d2f11e13c7daac30d748ed92c447b5042487ccdb723782ca14b95a9da203fff010b321"
                    .getBytes());
    private final PersonService personService;

    public JWT(PersonService personService) {
        this.personService = personService;
    }


    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365)) // 1 year
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<Person> getPersonFromToken(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return personService.findByUsername(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void clearCookies(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);
    }

    public void generateAccessTokenCookie(HttpServletResponse response, String username) {
        String token = generateAccessToken(username);
        Cookie accessTokenCookie = new Cookie("accessToken", token);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(1000 * 60 * 15); // 15 minutes
        response.addCookie(accessTokenCookie);
    }

    public void generateRefreshTokenCookie(HttpServletResponse response, String username) {
        String token = generateRefreshToken(username);
        Cookie refreshTokenCookie = new Cookie("refreshToken", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(1000 * 60 * 60 * 24 * 365); // 1 year
        response.addCookie(refreshTokenCookie);
    }
}
