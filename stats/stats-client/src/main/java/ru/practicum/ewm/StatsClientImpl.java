package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {

    private RestTemplate rest;

    private String serverUrl;

    public StatsClientImpl(@Value("http://stats-server:9090") String serverUrl) {
        this.serverUrl = serverUrl;
        this.rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

    }

    @Override
    public List<ViewStatDto> getStats(String start,
                                      String end,
                                      List<String> uris,
                                      Boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        ResponseEntity<List<ViewStatDto>> ewmServerResponse;
        try {
            ewmServerResponse = rest.exchange(
                    builder.build().toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (Exception e) {
            log.error("StatsClient error: message={}, stacktrace=", e.getMessage(), e);
            return Collections.emptyList();
        }
        return ewmServerResponse.getBody();
    }

    @Override
    public void postStat(StatDataCreateDto createDto) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/hit");
        HttpEntity<Object> requestEntity = new HttpEntity<>(createDto);

        try {
            rest.exchange(builder.build().toString(), HttpMethod.POST, requestEntity, Object.class);
        } catch (Exception e) {
            log.error("StatsClient error: message={}, stacktrace=", e.getMessage(), e);
        }
    }
}
