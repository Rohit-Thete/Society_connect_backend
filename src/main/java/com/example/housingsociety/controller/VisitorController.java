package com.example.housingsociety.controller;

import com.example.housingsociety.entity.User;
import com.example.housingsociety.entity.Visitor;
import com.example.housingsociety.service.UserService;
import com.example.housingsociety.service.VisitorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    private final VisitorService svc;
    private final UserService userService;

    public VisitorController(VisitorService svc, UserService userService){
        this.svc = svc;
        this.userService = userService;
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY')")
    public Visitor checkIn(@Valid @RequestBody Visitor v){ return svc.checkIn(v); }

    @PostMapping("/checkout/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY')")
    public Visitor checkOut(@PathVariable Integer id){ return svc.checkOut(id); }

    /** Admin can list all visitors */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY')")
    public List<Visitor> list(){ return svc.list(); }

    /** Existing: by flat */
    @GetMapping("/flat/{flatNo}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY','RESIDENT')")
    public List<Visitor> getByFlatNo(@PathVariable String flatNo) {
        return svc.getByFlatNo(flatNo);
    }

    /** NEW: visitors for the current logged-in user (uses user's flatNumber) */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('RESIDENT','ADMIN')")
    public List<Visitor> myVisitors(Authentication auth) {
        String email = auth.getName(); // username == email
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for " + email));

        String flatNo = user.getFlatNumber();
        if (flatNo == null || flatNo.isBlank()) {
            return List.of(); // no flat mapped -> nothing to show
        }
        return svc.getByFlatNo(flatNo);
    }
}
