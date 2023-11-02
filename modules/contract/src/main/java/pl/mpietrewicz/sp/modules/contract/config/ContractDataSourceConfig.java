package pl.mpietrewicz.sp.modules.contract.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
                "pl.mpietrewicz.sp.modules.contract.infrastructure.repo"
        },
        entityManagerFactoryRef = "contractEntityManagerFactory",
        transactionManagerRef = "contractTransactionManager"
)
public class ContractDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.contract")
    public DataSourceProperties contractDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource contractDataSource() {
        return contractDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean contractEntityManagerFactory (
            @Qualifier("contractDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("pl.mpietrewicz.sp.modules.contract.domain")
                .persistenceUnit("contract")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager contractTransactionManager (
            @Qualifier("contractEntityManagerFactory") LocalContainerEntityManagerFactoryBean contractEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(contractEntityManagerFactory.getObject()));
    }

}