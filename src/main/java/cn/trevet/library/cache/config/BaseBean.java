package cn.trevet.library.cache.config;

import cn.trevet.library.cache.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseBean {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
}
