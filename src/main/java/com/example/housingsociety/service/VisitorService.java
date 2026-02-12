package com.example.housingsociety.service;

import com.example.housingsociety.entity.Visitor;
import com.example.housingsociety.repository.VisitorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class VisitorService extends BaseService {
    private final VisitorRepository repo;
    public VisitorService(VisitorRepository repo){ this.repo = repo; }
    public Visitor checkIn(Visitor v){
        v.setInTime(LocalDateTime.now());
        return repo.save(v);
    }
    public Visitor checkOut(Integer id){
        Visitor v = repo.findById(id).orElseThrow(()->new RuntimeException("Visitor not found"));
        v.setOutTime(LocalDateTime.now());
        return repo.save(v);
    }
    public Optional<Visitor> get(Integer id){ return repo.findById(id); }
    public List<Visitor> list(){ return repo.findAll(); }
    public void delete(Integer id){ repo.deleteById(id); }
    public List<Visitor> getByFlatNo(String flatNo) {
        return repo.findByFlat_FlatNo(flatNo);
    }
}