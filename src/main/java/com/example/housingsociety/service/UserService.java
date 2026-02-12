package com.example.housingsociety.service;

import com.example.housingsociety.entity.User;
import com.example.housingsociety.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService extends BaseService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User create(User user) {
        if (user.getIsApproved() == null) user.setIsApproved(false);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return repo.save(user);
    }

    public User update(Integer id, User incoming) {
        Optional<User> opt = repo.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("User not found");
        User u = opt.get();

        u.setFullName(incoming.getFullName());
        u.setContact(incoming.getContact());
        u.setRole(incoming.getRole());
        // ⚠️ Approval and flat logic removed (now handled in UserApprovalService)

        return repo.save(u);
    }

    public Optional<User> getById(Integer id) {
        return repo.findById(id);
    }

    public List<User> list() {
        return repo.findAll();
    }

    public List<User> getByApprovalStatus(boolean isApproved) {
        return repo.findByIsApproved(isApproved);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
        );
    }
}
