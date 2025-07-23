package eatda.storage.image;

import eatda.domain.ImageDomain;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ImageStorage {

    private final ExternalImageStorage externalImageStorage;
    private final CachePreSignedUrlStorage cachePreSignedUrlStorage;


    public String upload(MultipartFile file, ImageDomain domain) {
        String imageKey = externalImageStorage.upload(file, domain);

        String preSignedUrl = externalImageStorage.getPresignedUrl(imageKey);
        cachePreSignedUrlStorage.put(imageKey, preSignedUrl);
        return imageKey;
    }

    @Nullable
    public String getPresignedUrl(@Nullable String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }

        Optional<String> cachedUrl = cachePreSignedUrlStorage.get(imageKey);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        String preSignedUrl = externalImageStorage.getPresignedUrl(imageKey);
        cachePreSignedUrlStorage.put(imageKey, preSignedUrl);
        return preSignedUrl;
    }
}
