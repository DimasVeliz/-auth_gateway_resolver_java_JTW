package com.boosting.code.auth_gateway_resolver.repositories;

import com.boosting.code.auth_gateway_resolver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
