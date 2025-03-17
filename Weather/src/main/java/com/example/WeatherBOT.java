package com.example;

import com.example.db.DataSource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WeatherBOT extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return DataSource.username;
    }
    @Override
    public String getBotToken() {
        return DataSource.token;
    }
}
