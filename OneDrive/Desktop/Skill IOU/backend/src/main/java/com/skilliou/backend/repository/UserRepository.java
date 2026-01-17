package com.skilliou.backend.repository;

import com.skilliou.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by email (useful for login/duplicate checks)
    boolean existsByEmail(String email);
}