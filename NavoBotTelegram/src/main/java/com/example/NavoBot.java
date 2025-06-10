package com.example;

import com.example.db.Datasource;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static java.awt.SystemColor.text;

public class NavoBot extends TelegramLongPollingBot {
    SendMessage sendMessage = new SendMessage();
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("https://www.instagram.com/")) {
                sendMessage(chatId, "⏳ Video yuklanmoqda, biroz kuting...");
                String filePath = downloadInstagramVideo(messageText);
                if (filePath != null) {
                    sendInstagramVideo(chatId, filePath);
                } else {
                    sendMessage(chatId, "❌ Video yuklab bo‘lmadi.");
                }
            } else {
                sendMessage(chatId, "⚠️ Instagram video havolasini yuboring.");
            }
        }
    }
    @SneakyThrows
    public void sendMessage(Long chatId, String s) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        execute(sendMessage);
    }

    @SneakyThrows
    public String downloadInstagramVideo(String url) {
        try {
            // Foydalanuvchi katalogiga saqlash
            String fileName = "instagram_video.mp4";
            String filePath = fileName; // Fayl saqlanadigan joy

            // yt-dlp orqali yuklab olish
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "yt-dlp", "-f", "best", "-o", filePath, url
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Natijani o‘qish
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();

            // Agar fayl mavjud bo‘lsa, yo‘lni qaytarish
            File file = new File(filePath);
            if (file.exists()) {
                return filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Xatolik yuz bersa null qaytadi
    }

    @SneakyThrows
    public void sendInstagramVideo(Long chatId, String filePath) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId.toString());
        sendVideo.setVideo(new InputFile(new File(filePath)));
        sendVideo.setCaption("📹 Instagram video");
        execute(sendVideo);
    }





    @Override
    public String getBotUsername() {
        return Datasource.userName;
    }
    public String getBotToken(){
        return Datasource.token;
    }
}
