package test;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.Map;

public class CacheService {

    private Map<String, CacheTestBean> beans = new HashMap<>();

    private static int value = 1;

    private static int cachedMethodCallCount = 0;

    @Cacheable("test-cache")
    public CacheTestBean getBean(String key) {
        if(!beans.containsKey(key)) {
            beans.put(key, new CacheTestBean(key, value++));
        }
        CacheTestBean bean = beans.get(key);
        cachedMethodCallCount++;
        return bean;
    }

    @CacheEvict(value = "test-cache", key = "#bean.key")
    public void insertBean(CacheTestBean bean) {
        beans.put(bean.getKey(), bean);
    }

    public void insertBeanChain(CacheTestBean bean) {
        //a chaining method does not trigger cache-evict
        insertBean(bean);
    }

    @CacheEvict(value = "test-cache", key = "#bean.key", allEntries = true)
    public void dismissCache() {
        value = 1;
        cachedMethodCallCount = 0;
        beans.clear();
    }

    public int getCachedMethodCallCount() {
        return cachedMethodCallCount;
    }

}
