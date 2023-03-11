package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.Category;
import com.lucas.redditclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameOrUri(String name, String uri);

    Optional<Category> findByUser(User user);
}