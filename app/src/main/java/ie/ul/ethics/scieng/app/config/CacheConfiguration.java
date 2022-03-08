package ie.ul.ethics.scieng.app.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This class configures caching in this module
 */
@Configuration
@EnableCaching
@EnableScheduling
@Log4j2
public class CacheConfiguration {
    /**
     * The cache manager
     */
    private final CacheManager cacheManager;

    /**
     * Create a cache configuration instance
     */
    @Autowired
    public CacheConfiguration(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Schedule the cache eviction
     */
    @Scheduled(fixedDelayString = "${app.cache.ttl}")
    public void scheduleCacheEvict() {
        log.info("Cache Time-to-Live (TTL) exceeded, evicting cache");

        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);

            if (cache != null)
                cache.clear();
        }
    }
}
