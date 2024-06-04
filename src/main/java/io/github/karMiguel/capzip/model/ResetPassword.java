package io.github.karMiguel.capzip.model;


import io.github.karMiguel.capzip.model.enums.StatusResetPassword;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_password")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private User user;

    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private StatusResetPassword status;

    @Column(length = 25)
    private String code;

    @Column(name = "new_password")
    private String newPassword;

    @CreatedDate
    @Column(name = "date_created",updatable = false)
    private LocalDateTime dateCreated;


    @LastModifiedDate
    @Column(name = "date_modification")
    private  LocalDateTime dateModification;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private  String modifiedBy;
}
