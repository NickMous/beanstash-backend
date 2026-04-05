package com.nickmous.beanstash.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nickmous.beanstash.domain.security.AuthorityService;
import com.nickmous.beanstash.entity.Authority;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.AuthorityRepository;
import com.nickmous.beanstash.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorityService authorityService;

    @Test
    void assignDefaultAuthorities_addsPackageReadToUser() {
        User user = new User();
        user.setUsername("newuser");

        Authority packageRead = new Authority();
        packageRead.setName("package:read");

        when(authorityRepository.findByName("package:read")).thenReturn(packageRead);

        authorityService.assignDefaultAuthorities(user);

        assertThat(user.getAuthorities()).contains(packageRead);
        verify(userRepository).save(user);
    }

    @Test
    void assignDefaultAuthorities_throwsWhenDefaultAuthorityMissing() {
        User user = new User();
        when(authorityRepository.findByName("package:read")).thenReturn(null);

        assertThatThrownBy(() -> authorityService.assignDefaultAuthorities(user))
            .isInstanceOf(IllegalStateException.class);
    }
}
