package ru.practicum.ewm.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDateTime created;

}
