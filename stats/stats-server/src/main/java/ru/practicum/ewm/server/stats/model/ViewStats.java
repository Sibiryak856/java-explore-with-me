package ru.practicum.ewm.server.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {

    private String appName;

    private String uri;

    private Long hits;

}
