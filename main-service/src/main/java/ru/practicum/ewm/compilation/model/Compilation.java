package ru.practicum.ewm.compilation.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMPILATIONS", schema = "PUBLIC")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPILATION_ID")
    private Long id;

    @JoinColumn(name = "COMPILATION_ID")
    private List<Long> events;

    @Column(name = "PINNED")
    private Boolean pinned;

    @Column(name = "TITLE")
    private String title;
}
