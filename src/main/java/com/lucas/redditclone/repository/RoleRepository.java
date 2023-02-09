package com.lucas.redditclone.repository;

import com.lucas.redditclone.entity.Role;
import com.lucas.redditclone.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
	Optional<Role> findByName(RoleName name);
}