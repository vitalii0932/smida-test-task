package org.example.smidatesttask.service;

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

/**
 * jwt service class
 */
@Service
public class JwtService {
    @Value("${secret.secret_key}")
    private String SECRET_KEY;

    /**
     * extract username from token function
     *
     * @param token - the token from request
     * @return the extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * extract claims from token function
     *
     * @param token - the token from request
     * @param claimsResolver - a Function<Claims, T> to resolve claims
     * @return claims from token
     * @param <T> - the type of the result obtained after resolving claims
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * generate a JWT token from the provided user details.
     *
     * @param userDetails - the user details used for generating the token
     * @return the generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * generate a JWT token from the provided user details and extracted claims.
     *
     * @param extractClaims - the extracted claims from token
     * @param userDetails - the user details used for generating the token
     * @return the generated JWT token.
     */
    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 72)) // how long does this token should be valid
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * check if token is valid function
     *
     * @param token - the token from request
     * @param userDetails - the user details used for generating the token
     * @return true if the token is valid, otherwise false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * check if token is expired now function
     *
     * @param token - the token from request
     * @return true if the token is expired, otherwise false.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * extract expiration date from token function
     *
     * @param token - the token from request
     * @return the date when the token expires
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * extract all claims from token function
     *
     * @param token - the token from request
     * @return all the claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * retrieves the signing key function
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}