package eatda.domain;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.Set;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class Image {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpg", "image/jpeg", "image/png");
    private static final String EXTENSION_DELIMITER = ".";
    private static final String DEFAULT_CONTENT_TYPE = "bin";

    private final ImageDomain domain;
    private final MultipartFile file;

    public Image(ImageDomain domain, @Nullable MultipartFile file) {
        validateContentType(file);
        this.domain = domain;
        this.file = file;
    }

    private void validateContentType(MultipartFile file) {
        if (file != null && !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    public String getExtension() {
        String filename = file.getOriginalFilename();
        if (filename == null
                || filename.lastIndexOf(EXTENSION_DELIMITER) == -1
                || filename.startsWith(EXTENSION_DELIMITER)
                || filename.endsWith(EXTENSION_DELIMITER)) {
            return DEFAULT_CONTENT_TYPE;
        }
        return filename.substring(filename.lastIndexOf(EXTENSION_DELIMITER) + 1);
    }

    public String getDomainName() {
        return domain.getName();
    }

    public boolean isEmpty() {
        return file == null || file.isEmpty();
    }
}
