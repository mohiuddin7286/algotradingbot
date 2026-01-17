package com.skilliou.backend.controller;

import com.skilliou.backend.model.User;
import com.skilliou.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Base URL: http://localhost:8080/api/users
@CrossOrigin(origins = "*") // Allows React to connect later
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. Create a new User
    @PostMapping
    public User createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        return userRepository.save(user);
    }

    // 2. Get all Users (for testing)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 3. Get User by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}