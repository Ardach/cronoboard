package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.dto.tag.TagCreateRequest;
import com.cronoboard.cronoboardbackend.entity.Tag;
import com.cronoboard.cronoboardbackend.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository repo;

    public TagController(TagRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Tag> list(@RequestParam Long userId) {
        return repo.findByUserIdOrderByNameAsc(userId);
    }

    @PostMapping
    public ResponseEntity<Tag> create(@Valid @RequestBody TagCreateRequest req) {
        Tag t = new Tag();
        t.setUserId(req.getUserId());
        t.setName(req.getName());
        t.setColorHex(req.getColorHex());
        return ResponseEntity.ok(repo.save(t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
