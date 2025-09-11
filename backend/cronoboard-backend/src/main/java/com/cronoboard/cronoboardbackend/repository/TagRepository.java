package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserIdOrderByNameAsc(Long userId);
}
