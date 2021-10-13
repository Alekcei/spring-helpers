package com.spring.helper.www;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@EnableWebMvc
@Configuration
public class ShareWWWConfigure implements WebMvcConfigurer {
    // https://newbedev.com/springboot-angular2-how-to-handle-html5-urls

    @Value("${www.path:}")
    private String wwwPath;

    @Value("${www.default-page:/index.html}")
    private String defaultStaticPage;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/*")
                .addResourceLocations(wwwPath)
                .setCacheControl(CacheControl.maxAge(Duration.ofMinutes(30)))
                .setUseLastModified(true)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {

                    @Override
                    protected Resource getResource(String resourcePath,
                                                   Resource location) throws IOException {

                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {

                            return requestedResource;
                        }

                        requestedResource = null;
                        if (resourcePath.lastIndexOf(".") < 0) {

                            requestedResource = location.createRelative(defaultStaticPage);
                        }

                        return requestedResource;
                    }
                });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (!isResources(handler)) {
                    return true;
                }
//                System.out.println("HandlerInterceptor.preHandle !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"  + handler.getClass().getName());
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
                if (!isResources(handler)) {
                    return;
                }

//                System.out.println("HandlerInterceptor.postHandle !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + handler.getClass().getName());
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
                if (!isResources(handler)) {
                    return;
                }

//                System.out.println("HandlerInterceptor.afterCompletion !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + handler.getClass().getName());
            }
        });

    }

    private boolean isResources(Object handler) {
        return handler instanceof ResourceHttpRequestHandler;
    }
}


interface WWWInterceptor extends HandlerInterceptor {

}