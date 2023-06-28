package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}")
            //application.properties 에서 설정한 uploadPath=file:///C:/shop/ 을 읽어온다.
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")//url이 /images/로 시작하는경우 위의 값을 기준으로 읽어옴
                .addResourceLocations(uploadPath);//로컬 컴퓨터에 저장된 파일을 읽어올 root 경로를 설정함.
    }
}
