package com.chq.coursearrange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger API 文档配置
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建 API 文档
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描所有 Controller
                .apis(RequestHandlerSelectors.basePackage("com.chq.coursearrange.controller"))
                .paths(PathSelectors.any())
                .build()
                // 添加 JWT Token 支持
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API 基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("排课系统 API 文档")
                .description("基于遗传算法的自动排课系统 RESTful API 接口文档")
                .version("2.0.0")
                .contact(new Contact(
                        "CourseArrange Team",
                        "https://github.com/your-repo/course-arrange",
                        "admin@example.com"
                ))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    /**
     * 配置 JWT Token 认证
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        securitySchemes.add(new ApiKey("JWT", "token", "header"));
        return securitySchemes;
    }

    /**
     * 配置安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build());
        return securityContexts;
    }

    /**
     * 默认的安全引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("JWT", authorizationScopes));
        return securityReferences;
    }
}
