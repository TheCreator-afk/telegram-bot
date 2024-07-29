package com.example.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        // Register our bot
        try {
            botsApi.registerBot(new Telegrambot());
            System.out.println("Bot is alive!");
        } catch (TelegramApiException e) {
            System.err.println("Failed to register bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}