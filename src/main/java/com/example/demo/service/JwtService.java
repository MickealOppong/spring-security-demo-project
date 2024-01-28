package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private Long jwtExpiration = 36000000L;

    public Long getJwtExpiration(){
        return jwtExpiration;
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public  <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSiginKey())
                .build()
                .parseClaimsJws((token))
                .getBody();
    }

    public Long getExpirationTime(){
        return jwtExpiration;
    }
    private Key getSiginKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username){
        return generateToken(new HashMap<>(),username);
    }

    public String generateToken(HashMap<String,Object> extractClaims,String username){
        return buildToken(extractClaims,username);
    }

    private String buildToken(Map<String, Object> extractClaims,String username) {
        return Jwts.builder().
                setClaims(extractClaims).
                setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+getJwtExpiration()))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
}
