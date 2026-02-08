package com.jwt.jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jwt.jwt_auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
