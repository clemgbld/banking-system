package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    public static final int TOKEN_BEGIN_INDEX = 7;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    private final String jwtSecret;

    private final DateProvider dateProvider;

    private final UserDetailsService userDetailsService;

    public JwtService(@Value("${jwt_secret}") String jwtSecret, DateProvider dateProvider, UserDetailsService userDetailsService) {
        this.jwtSecret = jwtSecret;
        this.dateProvider = dateProvider;
        this.userDetailsService = userDetailsService;
    }


    public void authenticate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (SecurityContextHolder.getContext().getAuthentication() != null) return;
        final String token = extractJwt(authHeader);
        extractUserName(token).ifPresent((userName) -> {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (!isTokenValid(token, userDetails)) return;
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        });
    }

    public boolean hasNoToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        return authHeader == null || !authHeader.startsWith(BEARER);
    }

    private String extractJwt(String authHeader) {
        return authHeader.substring(TOKEN_BEGIN_INDEX);
    }

    private Optional<String> extractUserName(String token) {
        return Optional.ofNullable(extractClaim(token, Claims::getSubject));
    }


    private boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractNonNullableUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .map(tokenIssuedDate -> tokenIssuedDate.before(Date.from(dateProvider.now())))
                .orElse(false);
    }


    private Optional<Date> extractExpiration(String token) {
        return Optional.ofNullable(extractClaim(token, Claims::getExpiration));
    }

    private String extractNonNullableUserName(String token) {
        return extractClaim(token, Claims::getSubject);
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
