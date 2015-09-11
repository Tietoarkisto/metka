package test;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.Map;

public class CacheService {

    private Map<String, CacheTestBean> beans = new HashMap<>();

    private static int value = 1;

    @Cacheable("test-cache")
    public CacheTestBean getBean(String key) {
        if(!beans.containsKey(key)) {
            beans.put(key, new CacheTestBean(key, value++));
        }
        CacheTestBean bean = beans.get(key);
        System.out.println("This should only be seen once.");
        return bean;
    }

    @CacheEvict(value = "test-cache", key = "#bean.key")
    public void insertBean(CacheTestBean bean) {
        beans.put(bean.getKey(), bean);
    }

}
