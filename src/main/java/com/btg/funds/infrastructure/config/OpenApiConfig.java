package com.btg.funds.infrastructure.config;

import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OpenApiConfig {

    @Bean
    @Profile("aws")
    public ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
        return serverBaseUrl -> serverBaseUrl + "/Prod";
    }
}
