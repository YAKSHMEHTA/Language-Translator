import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class TranslatorServer {

    // Put your NEW API key here
    static final String API_KEY = "sk_omcvorv5_BpoR2qwjKsiyqZsp06bc7NxD";

    static final String URL = "https://api.sarvam.ai/translate";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0);

        server.createContext("/translate", new TranslateHandler());

        server.setExecutor(null);

        System.out.println("Server running on:");
        System.out.println("http://localhost:8080/translate");

        server.start();
    }

    static class TranslateHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // CORS Headers
            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin",
                    "*");

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Methods",
                    "POST, OPTIONS");

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Headers",
                    "Content-Type");

            exchange.getResponseHeaders().add(
                    "Content-Type",
                    "application/json; charset=UTF-8");

            // Handle preflight request
            if (exchange.getRequestMethod()
                    .equalsIgnoreCase("OPTIONS")) {

                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // Allow only POST
            if (!exchange.getRequestMethod()
                    .equalsIgnoreCase("POST")) {

                String response =
                        "{ \"error\": \"Only POST allowed\" }";

                sendJson(exchange, 405, response);

                return;
            }

            try {

                // Read frontend JSON request
                InputStream is = exchange.getRequestBody();

                String requestBody =
                        new String(
                                is.readAllBytes(),
                                StandardCharsets.UTF_8);

                System.out.println("Frontend Request:");
                System.out.println(requestBody);

                // Extract values
                String text =
                        extract(requestBody, "text");

                String targetLang =
                        extract(requestBody, "target");

                // Translate
                String translated =
                        translate(text, targetLang);

                // Create JSON response
                String jsonResponse =
                        "{ \"translatedText\": \"" +
                        translated +
                        "\" }";

                sendJson(exchange, 200, jsonResponse);

            } catch (Exception e) {

                e.printStackTrace();

                String error =
                        "{ \"error\": \"" +
                        e.getMessage().replace("\"", "'") +
                        "\" }";

                sendJson(exchange, 500, error);
            }
        }
    }

    static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String json)
            throws IOException {

        byte[] bytes =
                json.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length);

        OutputStream os = exchange.getResponseBody();

        os.write(bytes);

        os.close();
    }

    static String translate(
            String text,
            String targetLang)
            throws Exception {

        String body =
                "{"
                + "\"input\": \"" + text + "\","
                + "\"source_language_code\": \"auto\","
                + "\"target_language_code\": \"" + targetLang + "\","
                + "\"speaker_gender\": \"Male\","
                + "\"mode\": \"formal\","
                + "\"model\": \"mayura:v1\","
                + "\"enable_preprocessing\": false"
                + "}";

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .header(
                                "Content-Type",
                                "application/json")
                        .header(
                                "api-subscription-key",
                                API_KEY)
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(body))
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        System.out.println("Sarvam Response:");
        System.out.println(response.body());

        // Extract translated text
        String marker = "\"translated_text\":\"";

        int start =
                response.body().indexOf(marker);

        if (start == -1) {
            throw new RuntimeException(
                    "translated_text not found in API response");
        }

        start += marker.length();

        int end =
                response.body().indexOf("\"", start);

        return response.body().substring(start, end);
    }

    static String extract(
            String json,
            String key) {

        String marker =
                "\"" + key + "\":\"";

        int start =
                json.indexOf(marker);

        if (start == -1) {
            return "";
        }

        start += marker.length();

        int end =
                json.indexOf("\"", start);

        return json.substring(start, end);
    }
}