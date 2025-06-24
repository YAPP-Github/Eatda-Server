package timeeat.controller.web.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtManager {

    private final JwtProperties jwtProperties;

    public JwtManager(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issueAccessToken(long id) {
        return createToken(id, jwtProperties.getAccessTokenExpiration(), TokenType.ACCESS_TOKEN);
    }

    public String issueRefreshToken(long id) {
        return createToken(id, jwtProperties.getRefreshTokenExpiration(), TokenType.REFRESH_TOKEN);
    }

    private String createToken(long identifier, Duration expiration, TokenType tokenType) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + expiration.toMillis());
        return Jwts.builder()
                .setSubject(Long.toString(identifier))
                .setIssuedAt(now)
                .setExpiration(expired)
                .claim("type", tokenType.name())
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public long resolveAccessToken(String accessToken) {
        return resolveToken(accessToken, TokenType.ACCESS_TOKEN);
    }

    public long resolveRefreshToken(String refreshToken) {
        return resolveToken(refreshToken, TokenType.REFRESH_TOKEN);
    }

    private long resolveToken(String token, TokenType tokenType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            validateTokenType(claims, tokenType);
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException exception) {
            // TODO : BusinessException 으로 변경 - EXPIRED_TOKEN("AUTH002", "이미 만료된 토큰입니다.");
            throw new RuntimeException("이미 만료된 토큰입니다.");
        } catch (Exception e) {
            // TODO : BusinessException 으로 변경 - UNAUTHORIZED_MEMBER("AUTH001", "인증되지 않은 회원입니다.");
            throw new RuntimeException("인증되지 않은 회원입니다.");
        }
    }

    private void validateTokenType(Claims claims, TokenType tokenType) {
        String extractTokenType = claims.get("type", String.class);
        if (!extractTokenType.equals(tokenType.name())) {
            // TODO : BusinessException 으로 변경 - UNAUTHORIZED_MEMBER("AUTH001", "인증되지 않은 회원입니다.");
            throw new RuntimeException("인증되지 않은 회원입니다.");
        }
    }
}
