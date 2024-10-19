package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final OpenAiService openAiService;
    private final List<Long> whitelist;

    // Konstruktor, um den OpenAiService zu übergeben und die Whitelist zu laden
    public TelegramBot(OpenAiService openAiService) {
        this.openAiService = openAiService;
        this.whitelist = loadWhitelistFromConfig();
    }

    @Override
    public String getBotUsername() {
        return ConfigLoader.getProperty("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return ConfigLoader.getProperty("TELEGRAM_ACCESS_KEY");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (isValidMessage(update)) {
            Long userId = getUserId(update);
            String chatId = getChatId(update);
            String userMessage = getUserMessage(update);

            logger.info("User ID: {}", userId);

            // Verarbeite die Nachricht, wenn die Benutzer-ID auf der Whitelist steht
            if (isUserAllowed(userId)) {
                handleAllowedUser(chatId, userMessage);
            } else {
                handleNotAllowedUser(chatId, userId);
            }
        }
    }

    // Überprüfe, ob die Nachricht gültig ist (ob sie Text enthält)
    private boolean isValidMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    // Extrahiere die Benutzer-ID aus dem Update
    private Long getUserId(Update update) {
        return update.getMessage().getFrom().getId();
    }

    // Extrahiere die Chat-ID aus dem Update
    private String getChatId(Update update) {
        return update.getMessage().getChatId().toString();
    }

    // Extrahiere die Benutzer-Nachricht aus dem Update
    private String getUserMessage(Update update) {
        return update.getMessage().getText();
    }

    // Überprüfe, ob der Benutzer auf der Whitelist steht
    private boolean isUserAllowed(Long userId) {
        return whitelist.contains(userId);
    }

    // Verarbeite die Nachricht von einem erlaubten Benutzer
    private void handleAllowedUser(String chatId, String userMessage) {
        try {
            String openAiResponse = openAiService.getResponse(userMessage);
            logger.info("OpenAI response: {}", openAiResponse);

            // Sende die Antwort an den Benutzer
            sendMessage(chatId, openAiResponse);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der OpenAI-Antwort: ", e);
        }
    }

    // Verarbeite die Nachricht von einem nicht erlaubten Benutzer
    private void handleNotAllowedUser(String chatId, Long userId) {
        logger.info("Benutzer mit der ID {} hat keinen Zugriff auf diesen Bot.", userId);
        sendMessage(chatId, "Sorry, du hast keinen Zugriff auf diesen Bot.");
    }

    // Nachricht an den Benutzer senden
    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message); // Nachricht senden
        } catch (TelegramApiException e) {
            logger.error("Fehler beim Senden der Nachricht: ", e);
        }
    }

    // Lese die Whitelist aus der config.properties
    private List<Long> loadWhitelistFromConfig() {
        String whitelistConfig = ConfigLoader.getProperty("WHITELIST_USER_IDS");
        // Konvertiere die durch Kommas getrennte String-Liste in eine Liste von Long-Werten
        return Arrays.stream(whitelistConfig.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
