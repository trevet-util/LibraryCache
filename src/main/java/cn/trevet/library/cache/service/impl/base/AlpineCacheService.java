package cn.trevet.library.cache.service.impl.base;

import cn.trevet.library.cache.config.BaseServerParam;
import cn.trevet.library.cache.config.ServerParam;
import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import cn.trevet.library.cache.service.IResourceCacheService;
import cn.trevet.library.cache.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

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
public class AlpineCacheService extends BaseCacheService implements IResourceCacheService {

    public AlpineCacheService(BaseServerParam serverParam, ResCacheTypeEnum typeEnum) {
        super(serverParam, typeEnum);
    }


    private static final String ALPINE_INDEX = "APKINDEX";

    /**
     * 获取资源
     *
     * @param outputStream 输出流
     * @param resURI       资源URI
     * @param version      版本
     * @throws IOException 异常
     */
    @Override
    public void getResource(ServletOutputStream outputStream, String resURI, String version) throws IOException {

        // 如果来源与版本信息中没有记录，则获取
        if (this.serverParam.getVersionInfoCache().isEmpty()) {
            this.versionChack(version, resURI);
            // 如果版本信息中没有记录，则抛出异常
            if (this.serverParam.getVersionInfoCache().isEmpty()) {
                throw new IOException("错误！远程服务器无法提供对应版本服务");
            }
        }


        File file;
        InputStream in = null;
        // 当前要访问的资源是否是索引包
        boolean isIndex = false;
        for (String s : this.serverParam.getVersionInfoCache().keySet()) {
            // 获取第一条链接相关的版本信息
            ServerParam.RemoteVersionInfo remoteVersionInfo = this.serverParam.getVersionInfoCache().get(s);
            file = new File(String.format("%s%s%s", super.serverParam.getCachePath(), remoteVersionInfo.getRealityVersion(), resURI));

            // 先判断APKINDEX索引包
            // 如果访问的是索引包，则允许直接获取索引包，而不是缓存
            if (resURI.contains(ALPINE_INDEX)) {
                isIndex = true;
            }

            // 判断文件是否不存在
            if (!file.exists()) {
                log.debug("文件不存在！");
                // 判断父目录是否不存在
                if (!file.getParentFile().exists()) {
                    FileUtils.forceMkdir(file.getParentFile());
                }
                // 获取URLConnection 对象
                String url = String.format("%s/v%s%s", remoteVersionInfo.getUrl(), remoteVersionInfo.getRealityVersion(), resURI);
                URLConnection con = new URL(url).openConnection();

                // 如果访问的是索引包，则允许直接获取索引包，而不是缓存
                if (isIndex) {
                    log.debug("索引包不缓存");
                    in = con.getInputStream();
                    IOUtils.copy(in, outputStream);
                    break;
                } else {
                    log.debug("非索引包则缓存并发送给请求者");
                    FileUtils.downFile(outputStream, con, file);
                    log.info("成功从仓库下载文件,仓库地址为: {}", url);
                }
                break;
            } else {
                in = Files.newInputStream(file.toPath());
                log.debug("命中本地缓存:{}", resURI);
                IOUtils.copy(in, outputStream);
                break;
            }
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(outputStream);
    }


    private void versionChack(String version, String resURI) throws IOException {
        // 能进入此代码块，则表示缺少RemoteVersionInfo信息
        for (String urlParam : this.serverParam.getRemoteUrl()) {
            // 如果URL最后以为是 / 则去掉
            String urlBase = urlParam.endsWith("/") ? urlParam.substring(0, urlParam.length() - 1) : urlParam;
            // 拼接版本信息
            String urlBaseVersion = String.format("%s/%s", urlBase, version);

            // 将版本信息通过小数点进行分割
            List<String> versionSplitList = Arrays.asList(version.split("\\."));
            // 将分割后的结果列表进行倒叙,以方便版本号从：1.2.3 在下一次循环时变成 1.2,直至远程匹配成功
            Collections.reverse(versionSplitList);

            URL url;
            for (String s : versionSplitList) {
                // 第一次循环时 versiontemp = 1.2.3 ，第二次循环时 versiontemp = 1.2。以此循环，直至找到一个可用的版本。
                String versiontemp = version.substring(0, version.lastIndexOf(s) + s.length());
                url = new URL(String.format("%s/v%s%s", this.serverParam.getRemoteUrl().get(0), versiontemp, resURI));
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if (httpURLConnection.getResponseCode() == 200) {
                        this.serverParam.getVersionInfoCache().put(urlBaseVersion, new ServerParam.RemoteVersionInfo(urlBase, version, versiontemp));
                        log.debug("初始化Alpine对应仓库 {} 的版本信息： {} => {}. 缓存信息创建完成", urlBase, version, versiontemp);
                        return;
                    }
                } catch (IOException e) {
                    log.error("创建HTTP连接出错：{}", e.getMessage());
                }
            }
        }
    }

}
