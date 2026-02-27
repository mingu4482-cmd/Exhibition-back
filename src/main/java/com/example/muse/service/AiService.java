package com.example.muse.service;

import com.example.muse.dto.AiAskRequest;
import com.example.muse.dto.AiAskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient webClient;

    public String callFastApiTest() {
        return webClient.get()
                .uri("http://fastapi:8000/ai/test")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public AiAskResponse askToFastApi(AiAskRequest req) {
        return webClient.post()
                .uri("http://fastapi:8000/ai/ask")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AiAskResponse.class)
                .block();
    }
}