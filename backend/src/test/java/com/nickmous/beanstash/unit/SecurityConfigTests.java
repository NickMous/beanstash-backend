package com.nickmous.beanstash.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nickmous.beanstash.domain.security.CustomUserDetailsService;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@email.com");
        testUser.setPassword("testpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        when(userRepository.findByUsername("testuser")).thenReturn(
            testUser
        );
    }

    @Test
    void testUserDetailsService_returnsCorrectUser() {
        // Arrange
        // CustomUserDetailsService above

        // Act
        UserDetails retrievedUser = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("testuser", retrievedUser.getUsername());
    }
}
