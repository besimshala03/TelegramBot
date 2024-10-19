package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    // Erstelle eine Whitelist mit zulässigen Benutzer-IDs
    private static final List<Long> WHITELIST = Arrays.asList(
            1561651501L, // Benutzer-ID 1
            5607517638L  // Benutzer-ID 2
    );

    @Override
    public String getBotUsername() {
        Properties config = loadConfig();
        return config.getProperty("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        Properties config = loadConfig();
        return config.getProperty("TELEGRAM_ACCESS_KEY");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            Long userId = update.getMessage().getFrom().getId();
            logger.info("User ID: {}", userId);

            // Überprüfe, ob die Benutzer-ID auf der Whitelist steht
            if (WHITELIST.contains(userId)) {
                // Zugriff erlauben, wenn die Benutzer-ID auf der Whitelist steht
                try {
                    String openAiResponse = callApi(userMessage);
                    logger.info(openAiResponse);

                    // Antwortnachricht erstellen
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(openAiResponse);

                    // Nachricht senden
                    execute(message);
                } catch (IOException | InterruptedException | TelegramApiException e) {
                    logger.error(String.valueOf(e));
                }
            } else {
                // Zugriff verweigern, wenn die Benutzer-ID nicht auf der Whitelist steht
                logger.info("Benutzer mit der ID {} hat keinen Zugriff auf diesen Bot.", userId);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Sorry, du hast keinen Zugriff auf diesen Bot.");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    logger.error(String.valueOf(e));
                }
            }
        }
    }

    private String callApi(String prompt) throws IOException, InterruptedException {
        String body = String.format("""
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

        Properties config = loadConfig();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getProperty("OPENAI_API_KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Die Antwort als JSON-Objekt parsen
        JSONObject jsonResponse = new JSONObject(response.body());

        // Den "content" aus der Antwort extrahieren und zurückgeben
        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    private static Properties loadConfig() {
        Properties config = new Properties();
        try (InputStream inputStream = MyTelegramBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file.", e);
        }
        return config;
    }
}
