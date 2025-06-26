package timeeat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] corsOrigin;

    public CorsConfig(@Value("${cors.origin}") String[] corsOrigin) {
        validate(corsOrigin);
        this.corsOrigin = corsOrigin;
    }

    private void validate(String[] corsOrigin) {
        if (corsOrigin == null || corsOrigin.length == 0) {
            // TODO Initialize error 논의
            throw new RuntimeException("Initialization Error: CORS origin cannot be empty.");
        }
        for (String origin : corsOrigin) {
            if (origin == null || origin.isBlank()) {
                throw new RuntimeException("Initialization Error: CORS origin string cannot be blank.");
            }
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsOrigin)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name()
                )
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}
