package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.SubReddit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubRedditRepository extends JpaRepository<SubReddit, UUID> {
	Optional<SubReddit> findByName(String name);

	@Query("select s from SubReddit s where upper(s.name) like upper(concat('%', ?1, '%'))")
	Page<SubReddit> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
}