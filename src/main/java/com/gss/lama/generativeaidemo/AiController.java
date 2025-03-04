package com.gss.lama.generativeaidemo;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author GSS
 */

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final OllamaApi ollamaApi;

    public AiController(@Value("${llama-url}") String baseUrl) {
        this.ollamaApi = new OllamaApi(baseUrl);
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        var request = OllamaApi.ChatRequest.builder("llama3")
                .stream(false)
                .messages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .content(message)
                                .build()))
                .options(OllamaOptions.builder().temperature(0.9).build())
                .build();

        OllamaApi.ChatResponse response = ollamaApi.chat(request);
        return response.message().content();
    }

    @GetMapping("/streaming-chat")
    public Flux<String> streamingChat(@RequestParam String message) {
        var request = OllamaApi.ChatRequest.builder("llama3")
                .stream(true)
                .messages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .content(message)
                                .build()))
                .options(OllamaOptions.builder().temperature(0.9).build().toMap())
                .build();

        return ollamaApi.streamingChat(request).map(res -> res.message().content());
    }
}