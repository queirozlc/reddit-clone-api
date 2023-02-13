package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.Comment;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
	Page<Comment> findAllByUser(User user, Pageable pageable);

	Page<Comment> findAllByPost(Post post, Pageable pageable);
}