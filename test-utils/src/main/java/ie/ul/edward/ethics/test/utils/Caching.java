package ie.ul.edward.ethics.test.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * This class provides caching functionality for testing
 */
@Component
public class Caching {
    /**
     * The cache manager used for caching
     */
    private final CacheManager cacheManager;

    /**
     * Create a caching object
     * @param cacheManager the cache manager used for caching
     */
    public Caching(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * This method clears all cache
     */
    public void clearCache() {
        cacheManager.getCacheNames().forEach(this::clearCache);
    }

    /**
     * This method clears all cache with the provided cache name
     * @param cacheName the name of the cache to clear
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null)
            cache.clear();
    }
}
