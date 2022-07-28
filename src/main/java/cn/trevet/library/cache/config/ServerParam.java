package cn.trevet.library.cache.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Data
@Slf4j
@Lazy
@Configuration
public class ServerParam implements ApplicationRunner {

    @Value("${res.config.maven.cache-path}")
    private String mvaenPath;
    @Value("#{'${res.config.maven.remote-url}'.split(',')}")
    private String[] mavenRemotePath;
    @Value("${res.config.alpine.cache-path}")
    private String alpinePath;
    @Value("${res.config.alpine.remote-url}")
    private String alpineRemotePath;

    /**
     * 源与版本对应的缓存信息
     */
    private Map<String, String> versionInfoCache = new HashMap<>();


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 判断是是否存在此文件
        FileUtils.forceMkdir(new File(this.mvaenPath));
    }
}
