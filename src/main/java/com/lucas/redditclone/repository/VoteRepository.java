package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
	Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User user);
}