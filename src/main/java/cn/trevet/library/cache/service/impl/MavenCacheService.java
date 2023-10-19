package cn.trevet.library.cache.service.impl;

import cn.trevet.library.cache.config.ServerParam;
import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import cn.trevet.library.cache.service.IResourceCacheService;
import cn.trevet.library.cache.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@Slf4j
@Service
public class MavenCacheService implements IResourceCacheService {
    @Autowired
    private ServerParam serverParam;

    @Override
    public boolean cache(ResCacheTypeEnum typeEnum) {
        return ResCacheTypeEnum.MAVEN.equals(typeEnum);
    }

    @Override
    public void getResource(ServletOutputStream outputStream, String resURI, String version) throws IOException {

        File file = new File(String.format("%s%s", this.serverParam.getMvaenPath(), resURI));
        InputStream in = null;
        if (!file.exists()) {
            log.debug("文件不存在！");
            // 判断父目录是否不存在
            if (!file.getParentFile().exists()) {
                FileUtils.forceMkdir(file.getParentFile());
            }
            for (String s : this.serverParam.getMavenRemotePath()) {
                try {
                    URL url = new URL(String.format("%s%s", s, resURI));
                    URLConnection con = url.openConnection();
                    FileUtils.downFile(outputStream, con, file);
                    log.info("ok！mirror: {} , Save path: {}", url, file.getPath());
                    break;
                } catch (IOException e) {
                    log.debug("上游仓库下载文件失败！: {}", String.format("%s%s", s, resURI));
                }
            }
        } else {
            in = Files.newInputStream(file.toPath());
            log.debug("命中缓存:{}", resURI);
            IOUtils.copy(in, outputStream);
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(outputStream);
    }

}
