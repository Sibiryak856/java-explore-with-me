package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countByEventIdAndStatusIs(long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdInAndStatusIs(List<Long> requestIds, RequestStatus pending);
}
