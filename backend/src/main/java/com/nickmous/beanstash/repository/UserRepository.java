package com.nickmous.beanstash.repository;

import com.nickmous.beanstash.entity.User;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID> {
    User findByUsername(String username);
}
