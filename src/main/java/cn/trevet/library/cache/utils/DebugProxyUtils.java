package cn.trevet.library.cache.utils;

import cn.trevet.library.cache.config.BaseServerParam;
import cn.trevet.library.cache.config.ServerParam;
import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class DebugProxyUtils {

    public static void proxy(HttpServletRequest request, HttpServletResponse response, String baseUri, ResCacheTypeEnum resCacheTypeEnum) {

        log.debug("RequestURL: {}", request.getRequestURL());
        log.debug("ServletPath: {}", request.getServletPath());
        log.debug("ContextPath: {}", request.getServletContext().getContextPath());
        log.debug("Method: {}", request.getMethod());
        log.debug("RequestURI: {}", request.getRequestURI());
        log.debug("Header: {}", request.getHeaderNames());
        log.debug("Header - user-agent: {}", request.getHeader("user-agent"));

        BaseServerParam baseServerParam = SpringContextUtil.getBean(BaseServerParam.class);
        ServerParam serverParam = baseServerParam.getParams().get(resCacheTypeEnum.getName());

        String uri = request.getRequestURI();
        String temp = uri.substring((request.getServletContext().getContextPath() + baseUri).length());

        String remoteUrl = serverParam.getRemoteUrl().get(0);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", request.getHeader("user-agent"));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String requestUrl = String.format("%s%s", remoteUrl, temp);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestUrl, String.class, entity);

        responseEntity.getHeaders().forEach((s, strings) -> response.addHeader(s, strings.get(0)));
        try {
            response.getWriter().print(responseEntity.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.debug("调试");


        log.debug("调试");
    }
}
