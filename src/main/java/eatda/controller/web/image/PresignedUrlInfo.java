package eatda.controller.web.image;

public record PresignedUrlInfo(
        long order,
        String contentType,
        String key,
        String url,
        long expiresIn
) {
}

