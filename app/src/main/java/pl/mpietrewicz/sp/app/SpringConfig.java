package pl.mpietrewicz.sp.app;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "pl.mpietrewicz.sp")
@EnableJpaRepositories(basePackages = "pl.mpietrewicz.sp")
@EntityScan(basePackages = "pl.mpietrewicz.sp")
public class SpringConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("user")
                .packagesToScan("pl.mpietrewicz.sp")
                .build();
    }

}