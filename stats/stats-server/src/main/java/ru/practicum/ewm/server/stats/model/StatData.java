package ru.practicum.ewm.server.stats.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STATS", schema = "PUBLIC")
public class StatData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STAT_ID")
    private Long id;

    @Column(name = "APP_NAME")
    private String appName;

    @Column(name = "URI")
    private String uri; //URI

    @Column(name = "IP")
    private String ip; //IP

    @Column(name = "CREATED_AT")
    private LocalDateTime created;

    @Transient
    private Long hits;

}
