package cn.trevet.library.cache.service;

import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import cn.trevet.library.cache.utils.SpringContextUtil;

public class ResourceCacheFactory {
    public static IResourceCacheService get(ResCacheTypeEnum typeEnum) {
        return SpringContextUtil.getApplicationContext().getBeansOfType(IResourceCacheService.class).values().stream().filter(iResourceCacheService -> iResourceCacheService.cache(typeEnum)).findFirst().orElse(null);
    }
}
