package vip.stqr.STShortURL.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * TODO
 * @author 曙天
 * @since 2022-09-24 01:58
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedOriginPatterns("*")
                .allowedHeaders("*");
    }
}
