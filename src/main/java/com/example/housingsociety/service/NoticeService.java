package com.example.housingsociety.service;

import com.example.housingsociety.entity.Notice;
import com.example.housingsociety.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class NoticeService extends BaseService {
    private final NoticeRepository repo;
    public NoticeService(NoticeRepository repo){ this.repo = repo; }
    public Notice create(Notice n){
        n.setCreatedAt(LocalDateTime.now());
        return repo.save(n);
    }
    public Optional<Notice> get(Integer id){ return repo.findById(id); }
    public List<Notice> list(){ return repo.findAll(); }
    public void delete(Integer id){ repo.deleteById(id); }
}