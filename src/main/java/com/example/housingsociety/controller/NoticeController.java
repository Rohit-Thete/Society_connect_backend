package com.example.housingsociety.controller;

import com.example.housingsociety.entity.Notice;
import com.example.housingsociety.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService svc;
    public NoticeController(NoticeService svc){ this.svc = svc; }
    @PostMapping public Notice create(@Valid @RequestBody Notice n){ return svc.create(n); }
    @GetMapping public List<Notice> list(){ return svc.list(); }
}