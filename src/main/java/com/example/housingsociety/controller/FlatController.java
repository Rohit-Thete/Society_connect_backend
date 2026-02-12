package com.example.housingsociety.controller;

import com.example.housingsociety.entity.Flat;
import com.example.housingsociety.dto.ApiResponse;
import com.example.housingsociety.service.FlatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flats")
public class FlatController {
    private final FlatService svc;
    public FlatController(FlatService svc){ this.svc = svc; }

    @PostMapping public ResponseEntity<?> create(@Valid @RequestBody Flat f){ return ResponseEntity.ok(svc.create(f)); }
    @GetMapping public List<Flat> list(){ return svc.list(); }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable String id){ return svc.get(id).map(x->ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build()); }
    @PutMapping("/{id}") public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody Flat f){ return ResponseEntity.ok(svc.update(id,f)); }
    @DeleteMapping("/{id}") public void delete(@PathVariable String id){ svc.delete(id); }
}