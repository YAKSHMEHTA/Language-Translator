package com.translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.net.URI;
import java.net.http.*;

@SpringBootApplication
@RestController
public class TranslatorServer {

    static final String API_KEY = "sk_omcvorv5_BpoR2qwjKsiyqZsp06bc7NxD";
    static final String URL = "https://api.sarvam.ai/translate";

    public static void main(String[] args) {
        SpringApplication.run(TranslatorServer.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }

    record TranslateRequest(String text, String target) {}
    record TranslateResponse(String translatedText) {}

    @PostMapping("/translate")
    public TranslateResponse translate(@RequestBody TranslateRequest req) throws Exception {
        String translated = callSarvam(req.text(), req.target());
        return new TranslateResponse(translated);
    }

    String callSarvam(String text, String targetLang) throws Exception {
        String body = """
                {
                  "input": "%s",
                  "source_language_code": "auto",
                  "target_language_code": "%s",
                  "speaker_gender": "Male",
                  "mode": "formal",
                  "model": "mayura:v1",
                  "enable_preprocessing": false
                }
                """.formatted(text, targetLang);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("api-subscription-key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var json = mapper.readTree(response.body());
        return json.get("translated_text").asText();
    }
}