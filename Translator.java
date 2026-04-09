import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Language	Code
// Assamese	as-IN
// Bengali	bn-IN
// Bodo	brx-IN
// Dogri	doi-IN
// English	en-IN
// Gujarati	gu-IN
// Hindi	hi-IN
// Kannada	kn-IN
// Kashmiri	ks-IN
// Konkani	kok-IN
// Maithili	mai-IN
// Malayalam	ml-IN
// Manipuri	mni-IN
// Marathi	mr-IN
// Nepali	ne-IN
// Odia	od-IN
// Punjabi	pa-IN
// Sanskrit	sa-IN
// Santali	sat-IN
// Sindhi	sd-IN
// Tamil	ta-IN
// Telugu	te-IN
// Urdu	ur-IN

public class Translator {

    static final String API_KEY = "sk_omcvorv5_BpoR2qwjKsiyqZsp06bc7NxD";
    static final String URL     = "https://api.sarvam.ai/translate";

    public static void main(String[] args) throws Exception {

        String textToTranslate = "my name is yaksh";
        String targetLanguage  = "hi-IN"; // Hindi

        String result = translate(textToTranslate, targetLanguage);
        System.out.println("Translated: " + result);
    }

    static String translate(String text, String targetLang) throws Exception {

        // Build the request body
        String body = "{"
            + "\"input\": \"" + text + "\","
            + "\"source_language_code\": \"auto\","
            + "\"target_language_code\": \"" + targetLang + "\","
            + "\"speaker_gender\": \"Male\","
            + "\"mode\": \"formal\","
            + "\"model\": \"mayura:v1\","
            + "\"enable_preprocessing\": false"
            + "}";

        // Send the HTTP request
        HttpClient client   = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL))
            .header("Content-Type", "application/json")
            .header("api-subscription-key", API_KEY)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Extract translated text from response
        String marker = "\"translated_text\":\"";
        int start = response.body().indexOf(marker) + marker.length();
        int end   = response.body().indexOf("\"", start);
        return response.body().substring(start, end);
    }
}