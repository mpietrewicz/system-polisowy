package pl.mpietrewicz.sp.app;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("user")
                .packagesToScan("pl.mpietrewicz.sp")
                .build();
    }

}