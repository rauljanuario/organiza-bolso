package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.user.UserResponse;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}
