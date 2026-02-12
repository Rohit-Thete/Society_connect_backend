package com.example.housingsociety.repository;

import com.example.housingsociety.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlatRepository extends JpaRepository<Flat, String> {}