package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    private final String jwtSecret;

    private final DateProvider dateProvider;

    public JwtService(@Value("${jwt_secret}") String jwtSecret, DateProvider dateProvider) {
        this.jwtSecret = jwtSecret;
        this.dateProvider = dateProvider;
    }


    public Optional<String> extractUserName(String token) {
        return Optional.ofNullable(extractClaim(token, Claims::getSubject));
    }


    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(dateProvider.now()))
                .setExpiration(getExpirationDate(dateProvider.now()))
                .signWith(getSignInKey(), SignatureAlgorithm.ES256)
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractNonNullableUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(dateProvider.now()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String extractNonNullableUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    private Date getExpirationDate(Instant instant) {
        Instant expirationDate = instant.plusMillis(1000 * 60 * 24);
        return Date.from(expirationDate);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}