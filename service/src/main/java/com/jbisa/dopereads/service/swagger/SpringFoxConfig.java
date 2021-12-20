package com.jbisa.dopereads.service.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jbisa.dopereads.service.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Dope Reads RESTful Web Service",
                "This service contains APIs for the Dope Reads web application.",
                "1",
                "",
                new Contact("Jay Bisa", "http://www.jaybisa.com", "jay.bisa@gmail.com"),
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
}
