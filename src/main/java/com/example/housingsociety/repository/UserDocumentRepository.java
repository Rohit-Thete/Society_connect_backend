package com.example.housingsociety.repository;

import com.example.housingsociety.entity.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Integer> {}