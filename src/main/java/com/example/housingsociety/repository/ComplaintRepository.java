package com.example.housingsociety.repository;

import com.example.housingsociety.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUser_UserId(Integer userId);
}