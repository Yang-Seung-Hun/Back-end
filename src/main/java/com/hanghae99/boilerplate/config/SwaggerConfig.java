package com.hanghae99.boilerplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@EnableSwagger2
@Configuration
public class SwaggerConfig {
    private static final String TITLE = "boilerplate";
    private static final String SPRING_VERSION = "SPRING BOOT -2.5.10";
    private static final String API_DESCRIPTION = "API LIST";

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2).select()
//
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    public ApiInfo apiInfo(){
//        return new ApiInfoBuilder()
//                .title(TITLE)
//                .version(SPRING_VERSION)
//                .description(API_DESCRIPTION)
//                .build();
//    }
}