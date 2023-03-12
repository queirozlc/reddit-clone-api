package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUser(User user);

    boolean existsByTokenOrUser(String token, User user);

    Optional<RefreshToken> findByToken(String token);
}