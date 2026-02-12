package com.example.housingsociety.service;

import com.example.housingsociety.entity.Flat;
import com.example.housingsociety.entity.FlatUser;
import com.example.housingsociety.entity.User;
import com.example.housingsociety.repository.FlatRepository;
import com.example.housingsociety.repository.FlatUserRepository;
import com.example.housingsociety.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserApprovedService {

    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final FlatUserRepository flatUserRepository;

    public UserApprovedService(UserRepository userRepository,
                               FlatRepository flatRepository,
                               FlatUserRepository flatUserRepository) {
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.flatUserRepository = flatUserRepository;
    }

    @Transactional
    public User approveResident(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsApproved()) {
            throw new RuntimeException("User already approved");
        }

        if (user.getFlatNumber() == null || user.getFlatNumber().isEmpty()) {
            throw new RuntimeException("Pending flat number is required for approval");
        }

        // Mark user as approved
        user.setIsApproved(true);

        // Extract flat number and fetch flat
        String flatNo = user.getFlatNumber();
        Flat flat = flatRepository.findById(flatNo)
                .orElseThrow(() -> new RuntimeException("Flat not found"));

        // Save user update
        User savedUser = userRepository.save(user);

        // Only create flat user if role is RESIDENT
        if (user.getRole() == User.Role.RESIDENT) {
            FlatUser flatUser = FlatUser.builder()
                    .user(user)
                    .flat(flat)
                    .residentType(FlatUser.ResidentType.OWNER) // Default type
                    .active(true)
                    .startDate(LocalDate.now())
                    .build();

            flatUserRepository.save(flatUser);
        }

        return savedUser;
    }

    @Transactional
    public void rejectUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: Delete from flat_users if exists
        flatUserRepository.findByUser(user).ifPresent(flatUserRepository::delete);

        // Delete user
        userRepository.delete(user);
    }
}
