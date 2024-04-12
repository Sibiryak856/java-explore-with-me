package ru.practicum.ewm.compilation.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.util.Set;

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

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
    mappedBy = "compilation")
    private Set<Event> events;

    @Column(name = "PINNED")
    private Boolean pinned;

    @Column(name = "TITLE")
    private String title;
}
