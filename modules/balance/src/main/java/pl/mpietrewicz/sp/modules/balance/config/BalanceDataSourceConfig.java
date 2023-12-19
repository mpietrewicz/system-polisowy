package pl.mpietrewicz.sp.modules.balance.config;

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
                "pl.mpietrewicz.sp.modules.balance.infrastructure.repo"
        },
        entityManagerFactoryRef = "balanceEntityManagerFactory",
        transactionManagerRef = "balanceTransactionManager"
)
public class BalanceDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.balance")
    public DataSourceProperties balanceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource balanceDataSource() {
        return balanceDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean balanceEntityManagerFactory (
            @Qualifier("balanceDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("pl.mpietrewicz.sp.modules.balance.domain")
                .persistenceUnit("balance")
                .build();
    }

    @Bean
    public PlatformTransactionManager balanceTransactionManager (
            @Qualifier("balanceEntityManagerFactory") LocalContainerEntityManagerFactoryBean balanceEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(balanceEntityManagerFactory.getObject()));
    }

}