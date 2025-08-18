package eatda.service.image;

import eatda.client.file.FileClient;
import eatda.controller.web.image.PresignedUrlInfo;
import eatda.controller.web.image.PresignedUrlRequest;
import eatda.controller.web.image.PresignedUrlResponse;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpg", "image/jpeg", "image/png", "image/gif");
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(10);
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 10;
    private static final String EXTENSION_DELIMITER = ".";
    private static final String FILE_PATH = "/temp/";

    private final FileClient fileClient;

    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        validateRequest(request);
        List<PresignedUrlInfo> urls = request.fileDetails().stream()
                .map(fileDetail -> {
                    validateContentType(fileDetail.contentType());
                    validateFileSize(fileDetail.fileSize());
                    String key = generateTempKey(fileDetail.contentType());
                    String tempUrl = fileClient.generateUploadPresignedUrl(key, PRESIGNED_URL_DURATION);
                    return new PresignedUrlInfo(
                            fileDetail.order(),
                            fileDetail.contentType(),
                            key,
                            tempUrl,
                            PRESIGNED_URL_DURATION.toSeconds());
                })
                .sorted(Comparator.comparingDouble(PresignedUrlInfo::order))
                .toList();
        return new PresignedUrlResponse(urls);
    }

    private void validateRequest(PresignedUrlRequest request) {
        if (request.fileDetails() == null || request.fileDetails().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_EMPTY_FILE_DETAILS);
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank() || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    private void validateFileSize(long fileSize) {
        if (fileSize <= 0 || fileSize > MAX_FILE_SIZE) {
            throw new BusinessException(BusinessErrorCode.INVALID_MAX_FILE_SIZE);
        }
    }

    private String generateTempKey(String contentType) {
        String fileExtension = contentType.substring(contentType.lastIndexOf("/") + 1);
        return FILE_PATH + UUID.randomUUID() + EXTENSION_DELIMITER + fileExtension;
    }
}
