package com.example.muse.controller;

import com.example.muse.dto.AiAskRequest;
import com.example.muse.dto.AiAskResponse;
import com.example.muse.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/api/ai/test")
    public String test() {
        return aiService.callFastApiTest();
    }

    @PostMapping("/api/ai/ask")
    public AiAskResponse ask(@RequestBody AiAskRequest req) {
        return aiService.askToFastApi(req);
    }
}