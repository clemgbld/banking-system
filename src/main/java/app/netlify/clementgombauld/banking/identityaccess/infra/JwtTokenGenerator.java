package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.identityaccess.domain.TokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final DateProvider dateProvider;

    private final String jwtSecret;

    @Autowired
    public JwtTokenGenerator(DateProvider dateProvider, @Value("${jwt_secret}") String jwtSecret) {
        this.dateProvider = dateProvider;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public String generate(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(dateProvider.now()))
                .setExpiration(getExpirationDate(dateProvider.now()))
                .signWith(getSignInKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date getExpirationDate(Instant instant) {
        Instant expirationDate = instant.plusMillis(1000 * 60 * 24);
        return Date.from(expirationDate);
    }
}
