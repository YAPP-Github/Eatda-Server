package eatda.storage.image;

import eatda.client.file.FileClient;
import eatda.domain.Image;
import eatda.domain.ImageKey;
import java.time.Duration;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ExternalImageStorage {

    private static final String PATH_DELIMITER = "/";
    private static final String EXTENSION_DELIMITER = ".";
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(30);

    private final FileClient fileClient;

    public ExternalImageStorage(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    public ImageKey upload(Image image) {
        String createdKey = createKey(image.getDomainName(), image.getExtension());
        fileClient.upload(image.getFile(), createdKey);
        return new ImageKey(createdKey);
    }

    private String createKey(String domainName, String extension) {
        String uuid = UUID.randomUUID().toString();
        return domainName + PATH_DELIMITER + uuid + EXTENSION_DELIMITER + extension;
    }

    public String getPreSignedUrl(ImageKey imageKey) {
        return fileClient.getPreSignedUrl(imageKey.getValue(), PRESIGNED_URL_DURATION);
    }
}
