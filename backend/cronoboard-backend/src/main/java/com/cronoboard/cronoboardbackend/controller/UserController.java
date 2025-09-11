package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.entity.User;
import com.cronoboard.cronoboardbackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        var now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return repo.save(user);
    }
}
