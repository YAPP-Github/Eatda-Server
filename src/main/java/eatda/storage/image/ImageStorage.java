package eatda.storage.image;

import eatda.domain.Image;
import eatda.domain.ImageKey;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageStorage {

    private final ExternalImageStorage externalImageStorage;
    private final CachePreSignedUrlStorage cachePreSignedUrlStorage;

    public ImageKey upload(Image image) {
        if (image == null || image.isEmpty()) {
            return new ImageKey(null);
        }

        ImageKey imageKey = externalImageStorage.upload(image);

        String preSignedUrl = externalImageStorage.getPreSignedUrl(imageKey);
        cachePreSignedUrlStorage.put(imageKey, preSignedUrl);
        return imageKey;
    }

    @Nullable
    public String getPreSignedUrl(@Nullable ImageKey imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }

        Optional<String> cachedUrl = cachePreSignedUrlStorage.get(imageKey);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        String preSignedUrl = externalImageStorage.getPreSignedUrl(imageKey);
        cachePreSignedUrlStorage.put(imageKey, preSignedUrl);
        return preSignedUrl;
    }
}
