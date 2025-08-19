package eatda.controller.web.image;

import java.util.List;

public record PresignedUrlResponse(
        List<PresignedUrlInfo> urls
) {
}
