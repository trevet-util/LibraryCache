package cn.trevet.library.cache.config;

import cn.trevet.library.cache.utils.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BaseBean {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
}
