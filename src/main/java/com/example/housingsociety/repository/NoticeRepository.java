package com.example.housingsociety.repository;

import com.example.housingsociety.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {}