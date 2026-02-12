package com.example.housingsociety.service;

import com.example.housingsociety.entity.Complaint;
import com.example.housingsociety.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class ComplaintService extends BaseService {
    private final ComplaintRepository repo;
    public ComplaintService(ComplaintRepository repo){ this.repo = repo; }
    public Complaint create(Complaint c){
        c.setCreatedAt(LocalDateTime.now());
        return repo.save(c);
    }
    public Complaint resolve(Integer id){
        Complaint c = repo.findById(id).orElseThrow(()->new RuntimeException("Not found"));
        c.setStatus(Complaint.Status.RESOLVED);
        c.setResolvedAt(LocalDateTime.now());
        return repo.save(c);
    }
    public Optional<Complaint> get(Integer id){ return repo.findById(id); }
    public List<Complaint> list(){ return repo.findAll(); }
    public void delete(Integer id){ repo.deleteById(id); }
    public List<Complaint> getByUserId(Integer userId) {
        return repo.findByUser_UserId(userId);
    }
}