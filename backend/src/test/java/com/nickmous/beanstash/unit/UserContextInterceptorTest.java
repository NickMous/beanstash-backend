package com.nickmous.beanstash.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nickmous.beanstash.config.UserContextHolder;
import com.nickmous.beanstash.config.UserContextInterceptor;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class UserContextInterceptorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserContextInterceptor interceptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        UserContextHolder.clear();
    }

    @Test
    void preHandle_setsUserIdWhenAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("testuser", "password", List.of())
        );

        interceptor.preHandle(request, response, new Object());

        assertEquals(userId.toString(), UserContextHolder.getUserId());
    }

    @Test
    void preHandle_doesNotSetUserIdWhenNotAuthenticated() throws Exception {
        interceptor.preHandle(request, response, new Object());

        assertNull(UserContextHolder.getUserId());
    }

    @Test
    void preHandle_doesNotSetUserIdForAnonymousAuthentication() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")))
        );

        interceptor.preHandle(request, response, new Object());

        assertNull(UserContextHolder.getUserId());
    }

    @Test
    void afterCompletion_clearsUserId() throws Exception {
        UserContextHolder.setUserId(UUID.randomUUID().toString());

        interceptor.afterCompletion(request, response, new Object(), null);

        assertNull(UserContextHolder.getUserId());
    }
}
