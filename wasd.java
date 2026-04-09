import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Language Translator using Sarvam AI API
 *
 * HOW TO RUN:
 *   javac SarvamTranslator.java
 *   java SarvamTranslator
 *
 * Supported language codes:
 *   en-IN  English
 *   hi-IN  Hindi
 *   bn-IN  Bengali
 *   gu-IN  Gujarati
 *   kn-IN  Kannada
 *   ml-IN  Malayalam
 *   mr-IN  Marathi
 *   od-IN  Odia
 *   pa-IN  Punjabi
 *   ta-IN  Tamil
 *   te-IN  Telugu
 */
public class wasd {

    private static final String API_KEY = "sk_omcvorv5_BpoR2qwjKsiyqZsp06bc7NxD"; 
    private static final String API_URL = "https://api.sarvam.ai/translate";

    public static void main(String[] args) throws Exception {

        // ---------- configure your translation here ----------
        String input          = "my name is yaksh";
        String sourceLang     = "auto";    // or e.g. "en-IN"
        String targetLang     = "hi-IN";   // Hindi
        String speakerGender  = "Male";    // "Male" or "Female"
        String mode           = "formal";  // "formal" or "code-mixed"
        // -----------------------------------------------------

        String translatedText = translate(input, sourceLang, targetLang, speakerGender, mode);
        System.out.println(translatedText);
    }

    public static String translate(String input,
                                   String sourceLangCode,
                                   String targetLangCode,
                                   String speakerGender,
                                   String mode) throws Exception {

        String jsonBody = "{"
            + "\"input\":"                + jsonString(input)          + ","
            + "\"source_language_code\":" + jsonString(sourceLangCode) + ","
            + "\"target_language_code\":" + jsonString(targetLangCode) + ","
            + "\"speaker_gender\":"       + jsonString(speakerGender)  + ","
            + "\"mode\":"                 + jsonString(mode)           + ","
            + "\"model\":\"mayura:v1\","
            + "\"enable_preprocessing\":false"
            + "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("api-subscription-key", API_KEY)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API Error " + response.statusCode() + ": " + response.body());
        }

        return extractField(response.body(), "translated_text");
    }

    // Pull a string field value out of a JSON response (no external lib needed)
    private static String extractField(String json, String field) {
        String marker = "\"" + field + "\":\"";
        int start = json.indexOf(marker);
        if (start == -1) return "Field not found: " + field;
        start += marker.length();

        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '"': sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    default:  sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Escape a Java string for embedding inside a JSON body
    private static String jsonString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}