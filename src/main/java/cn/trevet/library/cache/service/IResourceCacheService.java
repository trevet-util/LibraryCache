package cn.trevet.library.cache.service;

import cn.trevet.library.cache.enums.ResCacheTypeEnum;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * 资源缓存服务
 */
public interface IResourceCacheService {
    boolean cache(ResCacheTypeEnum typeEnum);

    /**
     * 获取资源主要入口
     *
     * @param outputStream 输出流
     * @param resURI       要访问的资源
     * @param version      版本号
     */
    void getResource(ServletOutputStream outputStream, String resURI, String version) throws IOException;
}
