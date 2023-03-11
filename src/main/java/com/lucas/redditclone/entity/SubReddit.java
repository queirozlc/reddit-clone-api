package com.lucas.redditclone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sub_reddit", schema = "reddit")
public class SubReddit implements Serializable {
    @Serial
    private static final long serialVersionUID = -6013330847504833010L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String uri;
    private String description;
    @OneToMany(mappedBy = "subReddit", orphanRemoval = true)
    private List<Post> posts;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    private Instant createdAt;
    private Instant updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubReddit subReddit = (SubReddit) o;
        return id != null && Objects.equals(id, subReddit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}