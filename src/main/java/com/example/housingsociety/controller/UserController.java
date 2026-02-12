package com.example.housingsociety.controller;

import com.example.housingsociety.dto.ApiResponse;
import com.example.housingsociety.entity.User;
import com.example.housingsociety.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        User u = svc.create(user);
        return ResponseEntity.ok(new ApiResponse("User created", true, u));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return svc.getById(id)
                .map(u -> ResponseEntity.ok(new ApiResponse("Found", true, u)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse("Not found", false, null)));
    }

    @GetMapping
    public List<User> list(@RequestParam(required = false) Boolean isApproved) {
        if (isApproved != null) {
            return svc.getByApprovalStatus(isApproved);
        }
        return svc.list();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody User user) {
        return ResponseEntity.ok(new ApiResponse("Updated", true, svc.update(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        svc.delete(id);
        return ResponseEntity.ok(new ApiResponse("Deleted", true, null));
    }
}
