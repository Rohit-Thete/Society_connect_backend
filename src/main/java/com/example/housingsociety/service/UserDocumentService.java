package com.example.housingsociety.service;

import com.example.housingsociety.entity.UserDocument;
import com.example.housingsociety.repository.UserDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class UserDocumentService extends BaseService {
    private final UserDocumentRepository repo;
    public UserDocumentService(UserDocumentRepository repo){ this.repo = repo; }
    public UserDocument create(UserDocument doc){
        doc.setUploadedAt(LocalDateTime.now());
        return repo.save(doc);
    }
    public Optional<UserDocument> get(Integer id){ return repo.findById(id); }
    public List<UserDocument> list(){ return repo.findAll(); }
    public void delete(Integer id){ repo.deleteById(id); }
}