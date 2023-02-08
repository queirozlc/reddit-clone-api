package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubRedditRepository extends JpaRepository<SubReddit, UUID> {
}