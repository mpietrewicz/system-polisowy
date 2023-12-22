package pl.mpietrewicz.sp.modules.finance.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "pl.mpietrewicz.sp.modules.finance.infrastructure.repo",
                "pl.mpietrewicz.sp.modules.finance.readmodel"
        },
        entityManagerFactoryRef = "financeEntityManagerFactory",
        transactionManagerRef = "financeTransactionManager"
)
public class FinanceDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.finance")
    public DataSourceProperties financeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource financeDataSource() {
        return financeDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean financeEntityManagerFactory (
            @Qualifier("financeDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("pl.mpietrewicz.sp.modules.finance.domain")
                .persistenceUnit("finance")
                .build();
    }

    @Bean
    public PlatformTransactionManager financeTransactionManager (
            @Qualifier("financeEntityManagerFactory") LocalContainerEntityManagerFactoryBean financeEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(financeEntityManagerFactory.getObject()));
    }

}