package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiService {

    // Öffentliche Methode zur Abfrage von OpenAI, die die anderen Methoden kombiniert
    public String getResponse(String prompt) throws IOException, InterruptedException {
        // Erstelle den JSON-Body der Anfrage
        String requestBody = createRequestBody(prompt);

        // Sende die Anfrage an OpenAI und erhalte die Antwort
        String jsonResponse = sendRequestToOpenAi(requestBody);

        // Parse die Antwort und extrahiere den relevanten Inhalt
        return parseResponse(jsonResponse);
    }

    // Erstellt den JSON-Body für die Anfrage an die OpenAI API
    private String createRequestBody(String prompt) {
        return String.format("""
        {
            "model": "gpt-4",
            "messages": [
                {
                    "role": "user",
                    "content": "%s"
                }
            ]
        }
        """, prompt);
    }

    // Sendet die Anfrage an OpenAI und gibt die Rohantwort als String zurück
    private String sendRequestToOpenAi(String requestBody) throws IOException, InterruptedException {
        // Erstelle das HTTP-Request-Objekt
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ConfigLoader.getProperty("OPENAI_API_KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // HTTP-Client erstellen und die Anfrage senden
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Rückgabe der API-Antwort
        return response.body();
    }

    // Parsed die JSON-Antwort von OpenAI und extrahiert den relevanten Inhalt
    private String parseResponse(String jsonResponseString) {
        // Erzeuge ein JSON-Objekt aus der Rohantwort
        JSONObject jsonResponse = new JSONObject(jsonResponseString);

        // Extrahiere den "content" der Antwort
        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
