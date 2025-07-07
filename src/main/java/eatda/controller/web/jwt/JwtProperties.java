package eatda.controller.web.jwt;

import java.time.Duration;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import eatda.exception.InitializeException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private static final int SECRET_KEY_MIN_BYTES = 32;

    private final byte[] secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtProperties(String secretKey, Duration accessTokenExpiration, Duration refreshTokenExpiration) {
        validate(secretKey);
        validate(accessTokenExpiration);
        validate(refreshTokenExpiration);

        this.secretKey = secretKey.getBytes();
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    private void validate(String secretKey) {
        if (secretKey == null || secretKey.getBytes().length < SECRET_KEY_MIN_BYTES) {
            throw new InitializeException("JWT secret key must be at least 32 bytes");
        }
    }

    private void validate(Duration expiration) {
        if (expiration == null) {
            throw new InitializeException("JWT token duration cannot be null");
        }
        if (expiration.isNegative()) {
            throw new InitializeException("JWT token duration must be positive");
        }
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }
}
