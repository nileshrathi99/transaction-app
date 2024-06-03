package dev.codescreen.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * This configuration class enables transaction management for the application using JPA.
 * Transaction management ensures data consistency by grouping database operations.
 * Either all operations within a transaction succeed, or all fail, maintaining data integrity.
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class TransactionalConfiguration {

    /**
     * This bean is responsible for managing database transactions within the application.
     * In this case, a `JpaTransactionManager` is created to manage transactions for JPA entities.
     *
     * @param entityManagerFactory The EntityManagerFactory bean used for JPA entity management.
     * @return A PlatformTransactionManager bean (JpaTransactionManager in this case).
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        log.info("Created JpaTransactionManager bean for managing JPA transactions");
        return transactionManager;
    }

}
