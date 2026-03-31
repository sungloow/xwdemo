package com.citycheckin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("城市打卡后台管理系统 API")
                        .description("重庆城市打卡系统后端接口文档。\n\n" +
                                "**认证说明**：调用受保护接口前，先调用 `/api/auth/login` 获取 token，" +
                                "然后点击右上角 **Authorize** 按钮，填写 `Bearer {token}` 即可。\n\n" +
                                "**演示账号**：\n" +
                                "- 超级管理员：admin / admin123\n" +
                                "- 江北区管理员：jb_admin / admin123\n" +
                                "- 渝中区管理员：yz_admin / admin123\n" +
                                "- 普通用户：user1 / user123")
                        .version("1.0.0")
                        .contact(new Contact().name("CityCheckin Team")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Token")
                                .description("登录后将返回的 token 填入，格式：Bearer {token}")));
    }
}
