package com.mes.mes.service;

import com.mes.mes.entity.User;
import com.mes.mes.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 평문 비밀번호 비교 (포트폴리오용).
     */
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                .filter(u -> password != null && password.equals(u.getPassword()));
    }
}
