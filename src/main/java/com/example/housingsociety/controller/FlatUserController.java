package com.example.housingsociety.controller;

import com.example.housingsociety.entity.FlatUser;
import com.example.housingsociety.service.FlatUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flat-users")
public class FlatUserController {

    private final FlatUserService svc;

    public FlatUserController(FlatUserService svc) {
        this.svc = svc;
    }

    // --- Admin CRUD ---

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public FlatUser create(@Valid @RequestBody FlatUser fu) {
        return svc.create(fu);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public List<FlatUser> list() {
        return svc.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public Optional<FlatUser> get(@PathVariable Integer id) {
        return svc.get(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public void delete(@PathVariable Integer id) {
        svc.delete(id);
    }

    // --- Resident/Admin: list flats linked to the logged-in user ---
    // Returns: [{ "flatNo": "A-101" }, { "flatNo": "B-204" }, ...]
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN','RESIDENT','ROLE_RESIDENT')")
    public List<Map<String, String>> myFlats(Principal principal) {
        String email = principal.getName();
        // Collect distinct flat numbers in a stable order
        var flatNos = svc.findByUserEmail(email).stream()
                .map(FlatUser::getFlat)
                .filter(Objects::nonNull)
                .map(f -> f.getFlatNo())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return flatNos.stream()
                .map(fn -> Map.of("flatNo", fn))
                .collect(Collectors.toList());
    }
}
