package com.nickmous.beanstash.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nickmous.beanstash.configuration.TestcontainersConfig;
import com.nickmous.beanstash.entity.AuditLog;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.AuditLogRepository;
import com.nickmous.beanstash.repository.UserRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ContextConfiguration(initializers = TestcontainersConfig.Initializer.class)
@ActiveProfiles("test")
public class LoggingTests {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAppCannotAddLogsManually() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        long initialCount = auditLogRepository.count();

        // Act
        AuditLog log = new AuditLog();
        log.setAction("CREATE");
        log.setActor(user);
        log.setLoggedAt(Instant.now());
        log.setDetails("Manually created log entry");
        log.setVersion(1L);
        log.setTableName("test_table");
        log.setRecordId("1");

        // Assert
        assertThrows(JpaSystemException.class, () -> auditLogRepository.save(log));

        long finalCount = auditLogRepository.count();
        Iterable<AuditLog> log1 = auditLogRepository.findAll();
        System.out.println("test");
        assertEquals(initialCount, finalCount);
    }

    @Test
    public void testLogsAreBeingAddedByPostgresForUserTable() {
        // Given
        long initialCount = auditLogRepository.count();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        long finalCount = auditLogRepository.count();
        assertEquals(initialCount + 1, finalCount);
    }
}
