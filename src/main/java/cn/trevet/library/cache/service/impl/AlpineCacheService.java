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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AlpineCacheService implements IResourceCacheService {

    @Autowired
    private ServerParam serverParam;

    @Override
    public boolean cache(ResCacheTypeEnum typeEnum) {
        return ResCacheTypeEnum.ALPINE.equals(typeEnum);
    }

    @Override
    public void getResource(ServletOutputStream outputStream, String resURI, String version) throws IOException {

        URLConnection con = this.getValidURL(version, resURI);
        InputStream in = null;
        if (con == null) {
            throw new IOException("urlConnection为空");
        }
        // 先判断APKINDEX索引包
        // 如果访问的是索引包，则允许直接获取索引包，而不是缓存
        String alpineIndex = "APKINDEX";
        if (resURI.contains(alpineIndex)) {
            in = con.getInputStream();
            IOUtils.copy(in, outputStream);
        } else {
            String saveURI = String.format("/v%s%s", this.serverParam.getVersionInfoCache().get(String.format("alpine%s", version)), resURI);
            File file = new File(String.format("%s%s", this.serverParam.getAlpinePath(), saveURI));
            // 判断文件是否存在，如果不存在，则判断文件所在目录是否存在。都不存在，则创建目录
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    FileUtils.forceMkdirParent(file);
                }
                FileUtils.downFile(outputStream, con, file);
            } else {
                in = Files.newInputStream(file.toPath());
                log.debug("命中缓存:{}", resURI);
                IOUtils.copy(in, outputStream);
            }
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(outputStream);
    }


    private HttpURLConnection getValidURL(String version, String resURI) throws IOException {

        // 先从版本缓存中获取可用版本
        String validVersion = this.serverParam.getVersionInfoCache().get(String.format("alpine%s", version));
        // 如果可用版本不为空，则直接使用此版本
        if (validVersion != null) {
            return (HttpURLConnection) new URL(String.format("%s/v%s%s", this.serverParam.getAlpineRemotePath(), validVersion, resURI)).openConnection();
        }
        List<String> versionSplitList = Arrays.asList(version.split("\\."));
        Collections.reverse(versionSplitList);
        URL url = null;
        for (String s : versionSplitList) {
            String versiontemp = version.substring(0, version.lastIndexOf(s) + s.length());
            log.info(String.format("/v%s%s", versiontemp, resURI));
            log.info("");
            url = new URL(String.format("%s/v%s%s", this.serverParam.getAlpineRemotePath(), versiontemp, resURI));
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200) {
                    this.serverParam.getVersionInfoCache().put(String.format("alpine%s", version), versiontemp);
                    return httpURLConnection;
                }
            } catch (IOException e) {
                log.error("创建HTTP连接出错：{}", e.getMessage());
            }
        }
        return null;
    }

}
