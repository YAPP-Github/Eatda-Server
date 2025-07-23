package eatda.storage.image;

import eatda.domain.ImageKey;
import eatda.repository.CacheSetting;
import java.util.Optional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CachePreSignedUrlStorage {

    private static final String CACHE_NAME = CacheSetting.IMAGE.getName();

    private final Cache cache;

    public CachePreSignedUrlStorage(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
    }

    public void put(ImageKey imageKey, String preSignedUrl) {
        cache.put(imageKey.getValue(), preSignedUrl);
    }

    public Optional<String> get(ImageKey imageKey) {
        return Optional.ofNullable(cache.get(imageKey.getValue(), String.class));
    }
}
