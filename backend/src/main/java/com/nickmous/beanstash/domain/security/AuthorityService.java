package com.nickmous.beanstash.domain.security;

import com.nickmous.beanstash.entity.Authority;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.AuthorityRepository;
import com.nickmous.beanstash.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorityService {

    private static final String DEFAULT_AUTHORITY = "package:read";

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public void assignDefaultAuthorities(User user) {
        Authority defaultAuthority = authorityRepository.findByName(DEFAULT_AUTHORITY);
        if (defaultAuthority == null) {
            throw new IllegalStateException("Default authority '" + DEFAULT_AUTHORITY + "' not found in database");
        }
        user.getAuthorities().add(defaultAuthority);
        userRepository.save(user);
    }
}
