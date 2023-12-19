package cn.trevet.library.cache.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Slf4j
public class ServerParam {
    private boolean enable = false;

    private String cachePath;

    private List<String> remoteUrl;

    /**
     * 源与版本对应的缓存信息
     */
    private Map<String, RemoteVersionInfo> versionInfoCache = new HashMap<>();

    @Data
    public static class RemoteVersionInfo {
        /**
         * 来源地址
         */
        private String url;
        /**
         * 请求版本号
         */
        private String requestVersion;
        /**
         * 实际远程版本号
         */
        private String realityVersion;

        public RemoteVersionInfo(String url, String requestVersion, String realityVersion) {
            this.url = url;
            this.requestVersion = requestVersion;
            this.realityVersion = realityVersion;
        }
    }
}
