package cn.trevet.library.cache.config;

import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import cn.trevet.library.cache.service.IResourceCacheService;
import cn.trevet.library.cache.service.impl.base.AlpineCacheService;
import cn.trevet.library.cache.service.impl.base.BaseCacheService;
import cn.trevet.library.cache.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceCacheBeans {

    @Bean
    public IResourceCacheService mavenCacheService(BaseServerParam serverParam) {
        return new BaseCacheService(serverParam, ResCacheTypeEnum.MAVEN);
    }

    @Bean
    public IResourceCacheService alpineCacheService(BaseServerParam serverParam) {
        return new AlpineCacheService(serverParam, ResCacheTypeEnum.ALPINE);
    }

    public static IResourceCacheService get(ResCacheTypeEnum typeEnum) {
        return SpringContextUtil.getApplicationContext().getBeansOfType(IResourceCacheService.class).values().stream().filter(iResourceCacheService -> iResourceCacheService.cache(typeEnum)).findFirst().orElse(null);
    }
}
