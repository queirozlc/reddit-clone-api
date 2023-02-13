package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
	@Query("select p from Post p where upper(p.title) like upper(concat('%', ?1, '%'))")
	Page<Post> findAllPostsByTitleIgnoreCase(String title, Pageable pageable);

	Page<Post> findAllByUser(User user, Pageable pageable);

	Page<Post> findAllBySubReddit(SubReddit subReddit, Pageable pageable);
}