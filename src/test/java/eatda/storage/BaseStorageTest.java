package eatda.storage;

import eatda.config.CacheConfig;
import org.springframework.cache.CacheManager;

public abstract class BaseStorageTest {

    private final CacheManager cacheManager = new CacheConfig().cacheManager();

    protected CacheManager getCacheManager() {
        return cacheManager;
    }
}
