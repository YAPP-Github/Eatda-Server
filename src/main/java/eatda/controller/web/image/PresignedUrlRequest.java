package eatda.controller.web.image;

import java.util.List;

public record PresignedUrlRequest(
        List<FileDetail> fileDetails
) {
    public record FileDetail(
            long order,
            String contentType,
            long fileSize
    ) {
    }
}
