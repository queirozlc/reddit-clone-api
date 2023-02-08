package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
	Optional<VerificationToken> findByToken(String token);
}