package com.lucas.redditclone.entity;

import com.lucas.redditclone.entity.enums.VoteType;
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
@Table(name = "vote", schema = "reddit")
public class Vote implements Serializable {
	@Serial
	private static final long serialVersionUID = -115229171141755340L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;
	@ToString.Include
	@Enumerated
	@Column(name = "vote_type")
	private VoteType voteType;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Vote vote = (Vote) o;
		return id != null && Objects.equals(id, vote.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}