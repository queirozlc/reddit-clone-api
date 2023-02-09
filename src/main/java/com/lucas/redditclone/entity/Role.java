package com.lucas.redditclone.entity;

import com.lucas.redditclone.entity.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "role", schema = "reddit")
public class Role implements Serializable {
	@Serial
	private static final long serialVersionUID = -1557160284749398896L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;
	@Enumerated(EnumType.STRING)
	private RoleName name;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Role role = (Role) o;
		return id != null && Objects.equals(id, role.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}