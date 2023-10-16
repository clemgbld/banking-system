package app.netlify.clementgombauld.banking.identityaccess.domain;

import java.util.Map;
import java.util.Optional;

public interface TokenService {
    Optional<String> extractUserName(String token);

    String generateToken(Map<String, Object> extraClaims, User user);

    boolean isTokenValid(String token, User user);
}
