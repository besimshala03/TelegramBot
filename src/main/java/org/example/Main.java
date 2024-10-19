package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) {
        // OpenAI Service initialisieren
        OpenAiService openAiService = new OpenAiService();

        // Telegram Bot initialisieren und mit dem OpenAI-Service verbinden
        TelegramBot telegramBot = new TelegramBot(openAiService);

        // Bots API initialisieren
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
