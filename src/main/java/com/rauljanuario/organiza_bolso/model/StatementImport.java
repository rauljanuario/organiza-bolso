package com.rauljanuario.organiza_bolso.model;

import com.rauljanuario.organiza_bolso.enums.FormatType;
import com.rauljanuario.organiza_bolso.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statement_imports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class StatementImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatType format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status;

    @Column(name = "imported_at", updatable = false)
    private LocalDateTime importedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
