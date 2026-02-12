package com.example.housingsociety.repository;

import com.example.housingsociety.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    List<Visitor> findByFlat_FlatNo(String flatNo);
}