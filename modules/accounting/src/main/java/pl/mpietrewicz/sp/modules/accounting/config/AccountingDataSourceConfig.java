package pl.mpietrewicz.sp.modules.accounting.config;

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
                "pl.mpietrewicz.sp.modules.accounting.infrastructure.repo"
        },
        entityManagerFactoryRef = "accountingEntityManagerFactory",
        transactionManagerRef = "accountingTransactionManager"
)
public class AccountingDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.accounting")
    public DataSourceProperties accountingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource accountingDataSource() {
        return accountingDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean accountingEntityManagerFactory (
            @Qualifier("accountingDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("pl.mpietrewicz.sp.modules.accounting.domain")
                .persistenceUnit("accounting")
                .build();
    }

    @Bean
    public PlatformTransactionManager accountingTransactionManager (
            @Qualifier("accountingEntityManagerFactory") LocalContainerEntityManagerFactoryBean accountingEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(accountingEntityManagerFactory.getObject()));
    }

}