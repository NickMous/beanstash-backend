package com.nickmous.beanstash.repository;

import com.nickmous.beanstash.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
