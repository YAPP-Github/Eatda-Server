package eatda.storage.image;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.ImageKey;
import eatda.repository.CacheSetting;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

class CachePreSignedUrlStorageTest {

    private CachePreSignedUrlStorage cachePreSignedUrlStorage;

    @BeforeEach
    void setUp() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(new ConcurrentMapCache(CacheSetting.IMAGE.getName())));
        cacheManager.initializeCaches();
        this.cachePreSignedUrlStorage = new CachePreSignedUrlStorage(cacheManager);
    }

    @Nested
    class PutAndGet {

        @Test
        void 넣은_값을_가져올_수_있다() {
            String key = "story/550e8400-e29b-41d4-a716-446655440000.jpg";
            String value = "https://example.com/presigned-url";
            cachePreSignedUrlStorage.put(new ImageKey(key), value);

            Optional<String> result = cachePreSignedUrlStorage.get(new ImageKey(key));

            assertThat(result).isPresent()
                    .hasValue(value);
        }

        @Test
        void 존재하지_않는_키를_가져오면_빈_옵셔널을_반환한다() {
            String key = "nonExistentKey";

            Optional<String> result = cachePreSignedUrlStorage.get(new ImageKey(key));

            assertThat(result).isEmpty();
        }
    }
}
