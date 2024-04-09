package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface RequestMapper {

    RequestDto toDto(Request request);

    List<RequestDto> toDtoList(List<Request> requests);

    default EventRequestStatusUpdateResult toStatusUpdateResult(List<Request> updatedRequest) {
        EventRequestStatusUpdateResult result =
                new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        updatedRequest.forEach(r -> {
            if (r.getStatus().equals(RequestStatus.CONFIRMED)) {
                result.getConfirmedRequests().add(toDto(r));
            } else if (r.getStatus().equals(RequestStatus.REJECTED)) {
                result.getRejectedRequests().add(toDto(r));
            }
        });
        return result;
    }
}
