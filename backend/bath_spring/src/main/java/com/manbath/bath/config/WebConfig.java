package com.manbath.bath.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       registry.addResourceHandler("/mkBath/img")
               .addResourceLocations("classpath:/static/img","img");
       registry.addResourceHandler("/")
               .addResourceLocations("classpath:/static/","statc");
       registry.addResourceHandler("/mkBath/css")
               .addResourceLocations("classpath:/static/css","css");
       registry.addResourceHandler("mkBath/js")
               .addResourceLocations("classpath:/static/js","js");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
