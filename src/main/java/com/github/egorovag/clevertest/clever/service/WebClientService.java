package com.github.egorovag.clevertest.clever.service;

import com.github.egorovag.clevertest.clever.dto.RequestBodyNote;
import com.github.egorovag.clevertest.clever.dto.ResponseClient;
import com.github.egorovag.clevertest.clever.dto.ResponseNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class WebClientService {

    public static final String URL_NOTES = "/notes";
    public static final String URL_CLIENTS = "/clients";

    private final WebClient webClient;

    public List<ResponseClient> getClients() {
        return webClient
                .post()
                .uri(URL_CLIENTS)
                .retrieve()
                .bodyToFlux(ResponseClient.class)
                .collectList()
                .block();
    }

    public List<ResponseNote> getNotes(List<RequestBodyNote> requestNotes) {
        return requestNotes.stream()
                .map(this::getNote)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public List<ResponseNote> getNote(RequestBodyNote requestBodyNote) {
        return webClient
                .post()
                .uri(URL_NOTES)
                .bodyValue(requestBodyNote)
                .retrieve()
                .bodyToFlux(ResponseNote.class)
                .collectList()
                .block();
    }
}
