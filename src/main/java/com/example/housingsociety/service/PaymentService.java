package com.example.housingsociety.service;

import com.example.housingsociety.entity.Payment;
import com.example.housingsociety.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class PaymentService extends BaseService {
    private final PaymentRepository repo;
    public PaymentService(PaymentRepository repo){ this.repo = repo; }
    public Payment create(Payment p){
        p.setPaymentDate(LocalDateTime.now());
        return repo.save(p);
    }
    public Optional<Payment> get(Integer id){ return repo.findById(id); }
    public List<Payment> list(){ return repo.findAll(); }
    public void delete(Integer id){ repo.deleteById(id); }
}