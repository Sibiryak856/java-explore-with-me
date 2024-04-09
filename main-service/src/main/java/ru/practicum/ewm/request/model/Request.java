package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.request.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REQUESTS", schema = "PUBLIC")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Long requesterId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "CREATED")
    private LocalDateTime created;
}
