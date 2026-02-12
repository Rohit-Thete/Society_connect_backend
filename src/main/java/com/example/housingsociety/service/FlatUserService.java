package com.example.housingsociety.service;

import com.example.housingsociety.entity.FlatUser;
import com.example.housingsociety.repository.FlatUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlatUserService extends BaseService {

    private final FlatUserRepository repo;

    public FlatUserService(FlatUserRepository repo) {
        this.repo = repo;
    }

    public FlatUser create(FlatUser fu) {
        return repo.save(fu);
    }

    public FlatUser update(Integer id, FlatUser incoming) {
        // Basic upsert; adjust if you need field-by-field updates
        incoming.setId(id);
        return repo.save(incoming);
    }

    public Optional<FlatUser> get(Integer id) {
        return repo.findById(id);
    }

    public List<FlatUser> list() {
        return repo.findAll();
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    // ---- Helpers for controllers ----
    public List<FlatUser> findByUserEmail(String email) {
        return repo.findByUser_Email(email);
    }

    public List<FlatUser> findByFlatNo(String flatNo) {
        return repo.findByFlat_FlatNo(flatNo);
    }
}
