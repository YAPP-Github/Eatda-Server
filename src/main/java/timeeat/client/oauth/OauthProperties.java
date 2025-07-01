package timeeat.client.oauth;

import java.net.URI;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
            // TODO InitializeException 을 이용
            throw new RuntimeException("Client ID must not be null or empty");
        }
    }

    private void validateRedirectPath(String redirectPath) {
        if (redirectPath == null || !redirectPath.startsWith("/")) {
            // TODO InitializeException 을 이용
            throw new RuntimeException("Redirect path must not be null or start with '/'");
        }
    }

    private void validateOrigins(List<String> origins) {
        if (origins == null || origins.isEmpty()) {
            // TODO InitializeException 을 이용
            throw new RuntimeException("Allowed origins must not be null or empty");
        }
        origins.forEach(this::validateOrigin);
    }

    private void validateOrigin(String origin) {
        URI uri;
        try {
            uri = new URI(origin);
        } catch (Exception e) {
            // TODO InitializeException 을 이용
            throw new RuntimeException("Allowed origin must be a valid origin form: " + origin, e);
        }

        if (uri.getScheme() == null || uri.getHost() == null || !uri.getPath().isBlank()) {
            // TODO InitializeException 을 이용
            System.out.println("Path: " + uri.getPath());
            throw new RuntimeException("Allowed origin must be a valid origin form: " + origin);
        }
    }

    public boolean isAllowedOrigin(String origin) {
        return allowedOrigins.stream()
                .anyMatch(allowedOrigin -> allowedOrigin.equalsIgnoreCase(origin.trim()));
    }
}
