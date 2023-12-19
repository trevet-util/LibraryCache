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
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@Slf4j
public class BaseCacheService implements IResourceCacheService {
    private final ResCacheTypeEnum typeEnum;

    final ServerParam serverParam;

    public BaseCacheService(BaseServerParam serverParam, ResCacheTypeEnum typeEnum) {
        // 注册Bean时，先保证本类获取的参数是有效的
        this.serverParam = serverParam.getParams().get(typeEnum.getName());
        // 标记类型
        this.typeEnum = typeEnum;
    }

    /**
     * 检查是否属于对应处理方式
     *
     * @param typeEnum 类型枚举
     */
    @Override
    public boolean cache(ResCacheTypeEnum typeEnum) {
        return this.typeEnum.equals(typeEnum);
    }

    @Override
    public boolean isEnabled() {
        return this.serverParam.isEnable();
    }

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

        File file = new File(String.format("%s%s", this.serverParam.getCachePath(), resURI));
        InputStream in = null;
        if (!file.exists()) {
            log.debug("文件不存在！");
            // 判断父目录是否不存在
            if (!file.getParentFile().exists()) {
                FileUtils.forceMkdir(file.getParentFile());
            }
            for (String s : this.serverParam.getRemoteUrl()) {
                try {
                    URL url = new URL(String.format("%s%s", s, resURI));
                    URLConnection con = url.openConnection();
                    FileUtils.downFile(outputStream, con, file);
                    log.info("成功从仓库下载文件,仓库地址为: {} , 保存文件路径为: {}", url, file.getPath());
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
