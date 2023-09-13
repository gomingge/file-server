package com.xing.fileserver.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot Minio文件服务项目接口文档")
                .description("SpringBoot & Minio")
                .version("1.0"));
    }
}
