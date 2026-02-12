package com.example.housingsociety.service;

import com.example.housingsociety.entity.Flat;
import com.example.housingsociety.repository.FlatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlatService extends BaseService {
    private final FlatRepository repo;

    public FlatService(FlatRepository repo) {
        this.repo = repo;
    }

    public Flat create(Flat flat) {
        flat.setCreatedAt(LocalDateTime.now());
        // if isActive null, default true
        if (flat.getIsActive() == null) {
            flat.setIsActive(true);
        }
        return repo.save(flat);
    }

    public Flat update(String id, Flat incoming) {
        Optional<Flat> opt = repo.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Flat not found");
        Flat f = opt.get();

        // Update fields that are allowed to change
        f.setBlock(incoming.getBlock());
        f.setFloor(incoming.getFloor());
        f.setSizeSqft(incoming.getSizeSqft());

        // ✅ Allow toggling active status if provided
        if (incoming.getIsActive() != null) {
            f.setIsActive(incoming.getIsActive());
        }

        f.setUpdatedAt(LocalDateTime.now());
        return repo.save(f);
    }

    public Optional<Flat> get(String id) { return repo.findById(id); }

    public List<Flat> list() { return repo.findAll(); }

    public void delete(String id) { repo.deleteById(id); }
}
