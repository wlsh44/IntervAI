package wlsh.project.intervai.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    @Profile("prod")
    public UrlBasedCorsConfigurationSource corsConfigurationSourceProd() {
        CorsConfiguration configuration = getCorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                )
        );
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Profile("!prod")
    public UrlBasedCorsConfigurationSource corsConfigurationSourceLocal() {
        CorsConfiguration configuration = getCorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        return configuration;
    }
}
