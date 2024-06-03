package dev.transactionapp.config;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionalConfigurationTest {

    @InjectMocks
    private TransactionalConfiguration transactionalConfiguration;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void testTransactionManagerBeanCreation() {

        PlatformTransactionManager transactionManager = transactionalConfiguration.transactionManager(entityManagerFactory);
        assertNotNull(transactionManager);
        assertTrue(transactionManager instanceof JpaTransactionManager);
    }
}