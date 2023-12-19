package cn.trevet.library.cache.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

/**
 * 基础服务配置
 */
@Data
@Slf4j
@Lazy
@Configuration
@ConfigurationProperties(prefix = "res.config")
public class BaseServerParam {
    /**
     * 缓存前缀
     */
    private String cachePrefix;
    /**
     * 缓存服务配置
     */
    private Map<String, ServerParam> params;

}
