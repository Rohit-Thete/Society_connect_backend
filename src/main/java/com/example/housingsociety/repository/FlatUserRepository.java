package com.example.housingsociety.repository;

import com.example.housingsociety.entity.FlatUser;
import com.example.housingsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlatUserRepository extends JpaRepository<FlatUser, Integer> {

    Optional<FlatUser> findByUser(User user);

    // All occupants/owners for a flat
    List<FlatUser> findByFlat_FlatNo(String flatNo);

    // All flat-user rows for a given user email
    List<FlatUser> findByUser_Email(String email);
}
