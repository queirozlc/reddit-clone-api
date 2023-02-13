package com.lucas.redditclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "post", schema = "reddit")
public class Post implements Serializable {
	@Serial
	private static final long serialVersionUID = -2356609617822079630L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;
	private String title;
	private String url;
	@Column(columnDefinition = "TEXT")
	private String body;
	private Integer voteCount;
	private Instant createdAt;
	private Instant updatedAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_reddit_id")
	private SubReddit subReddit;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Post post = (Post) o;

		return id.equals(post.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}