package com.example.housingsociety.controller;

import com.example.housingsociety.entity.Payment;
import com.example.housingsociety.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService svc;
    public PaymentController(PaymentService svc){ this.svc = svc; }
    @PostMapping public Payment create(@Valid @RequestBody Payment p){ return svc.create(p); }
    @GetMapping public List<Payment> list(){ return svc.list(); }
}