package io.github.karMiguel.capzip.model.click;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.karMiguel.capzip.model.linkShort.LinkShort;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "click")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Click implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String localization;

    @CreatedDate
    @Column(name = "date_created", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_short_id", referencedColumnName = "id")
    private LinkShort linkShort;

    @PrePersist
    public void prePersist() {
        this.dateCreated = LocalDateTime.now();
    }

}
