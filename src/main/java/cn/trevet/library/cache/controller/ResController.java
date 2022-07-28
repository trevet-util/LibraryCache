package cn.trevet.library.cache.controller;

import cn.trevet.library.cache.enums.ResCacheTypeEnum;
import cn.trevet.library.cache.service.ResourceCacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class ResController {
    private final String uriMaven = "/maven/agent/cache";

    @GetMapping(uriMaven + "/**")
    public void maven(HttpServletRequest request, HttpServletResponse response) {
        log.info(request.getRequestURL().toString());
        String resURI = request.getServletPath().substring(uriMaven.length());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ResourceCacheFactory.get(ResCacheTypeEnum.MAVEN).getResource(outputStream, resURI, null);
            response.setContentType("application/x-java-archive");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private final String uriAlpine = "/alpine/agent/cache";

    @GetMapping(uriAlpine + "/v{version}/**")
    public void alpine(HttpServletRequest request, HttpServletResponse response, @PathVariable String version) {
        log.debug("调试模式");
        log.info(request.getRequestURL().toString());
        String resURI = request.getServletPath().substring(uriAlpine.length() + ("/v" + version).length());

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ResourceCacheFactory.get(ResCacheTypeEnum.ALPINE).getResource(outputStream, resURI, version);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
