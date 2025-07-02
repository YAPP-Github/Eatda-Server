package timeeat.client.oauth;

import java.net.URI;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import timeeat.exception.InitializeException;

@Getter
@ConfigurationProperties(prefix = "oauth")
public class OauthProperties {

    private final String clientId;
    private final String redirectPath;
    private final List<String> allowedOrigins;

    public OauthProperties(String clientId, String redirectPath, List<String> allowedOrigins) {
        validateClientId(clientId);
        validateRedirectPath(redirectPath);
        validateOrigins(allowedOrigins);

        this.clientId = clientId;
        this.redirectPath = redirectPath;
        this.allowedOrigins = allowedOrigins;
    }

    private void validateClientId(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new InitializeException("Client ID must not be null or empty");
        }
    }

    private void validateRedirectPath(String redirectPath) {
        if (redirectPath == null || !redirectPath.startsWith("/")) {
            throw new InitializeException("Redirect path must not be null or start with '/'");
        }
    }

    private void validateOrigins(List<String> origins) {
        if (origins == null || origins.isEmpty()) {
            throw new InitializeException("Allowed origins must not be null or empty");
        }
        origins.forEach(this::validateOrigin);
    }

    private void validateOrigin(String origin) {
        URI uri;
        try {
            uri = new URI(origin);
        } catch (Exception e) {
            throw new InitializeException("Allowed origin must be a valid origin form: " + origin, e);
        }

        if (uri.getScheme() == null || uri.getHost() == null || !uri.getPath().isBlank()) {
            throw new InitializeException("Allowed origin must be a valid origin form: " + origin);
        }
    }

    public boolean isAllowedOrigin(String origin) {
        return allowedOrigins.stream()
                .anyMatch(allowedOrigin -> isMatchedOrigin(allowedOrigin, origin));
    }

    private boolean isMatchedOrigin(String allowedOrigin, String origin) {
        return origin.trim().equals(allowedOrigin)
                || origin.trim().equals(allowedOrigin + "/");
    }
}
