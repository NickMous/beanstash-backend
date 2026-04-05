package com.nickmous.beanstash.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nickmous.beanstash.config.UserContextHolder;
import com.nickmous.beanstash.configuration.TestcontainersConfig;
import com.nickmous.beanstash.entity.AuditLog;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.AuditLogRepository;
import com.nickmous.beanstash.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ContextConfiguration(initializers = TestcontainersConfig.Initializer.class)
@ActiveProfiles("test")
public class AuditUserContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void auditLogCapturesSystemUserWhenNoUserContext() {
        User user = new User();
        user.setUsername("no_context_user");
        user.setPassword("password");
        user.setFirstName("No");
        user.setLastName("Context");
        userRepository.save(user);

        AuditLog log = auditLogRepository.findAll().iterator().next();
        assertEquals("00000000-0000-0000-0000-000000000000", log.getActor().getId().toString());
    }

    @Test
    void auditLogCapturesAuthenticatedUserWhenUserContextIsSet() {
        // Create the user who will act as the authenticated user
        User actor = new User();
        actor.setUsername("actor_user");
        actor.setPassword("password");
        actor.setFirstName("Actor");
        actor.setLastName("User");
        User savedActor = userRepository.save(actor);

        // Set the user context as if the interceptor did it
        UserContextHolder.setUserId(savedActor.getId().toString());

        // Create another user — this should be logged with actor as the actor
        User target = new User();
        target.setUsername("target_user");
        target.setPassword("password");
        target.setFirstName("Target");
        target.setLastName("User");
        userRepository.save(target);

        // Find the audit log for the target user
        AuditLog log = null;
        for (AuditLog entry : auditLogRepository.findAll()) {
            if (entry.getRecordId().equals(target.getId().toString())) {
                log = entry;
                break;
            }
        }

        assertNotNull(log, "Expected audit log entry for target user but none was found");
        assertEquals(savedActor.getId(), log.getActor().getId());
    }
}
