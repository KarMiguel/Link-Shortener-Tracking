package io.github.karMiguel.capzip.model.linkShort;

import io.github.karMiguel.capzip.model.click.Click;
import io.github.karMiguel.capzip.model.users.Users;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "link_short")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LinkShort implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_link", nullable = false, unique = true)
    private String shortLink;

    @Column(name = "link_long", nullable = false, columnDefinition = "TEXT")
    private String linkLong;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @CreatedDate
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;
}
