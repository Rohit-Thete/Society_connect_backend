package com.example.housingsociety.repository;

import com.example.housingsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByIsApproved(boolean isApproved);



    List<User> findByRole(User.Role role);
}