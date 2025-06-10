package com.example;

import com.example.db.DataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.example.db.DataSource.WEATHER_API_KEY;

public class WeatherBOT extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return DataSource.username;
    }

    @Override
    public String getBotToken() {
        return DataSource.token;
    }
    private static final String WEATHERBIT_API_KEY = WEATHER_API_KEY; // Weatherbit API kaliti

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, String> userStateMap = new HashMap<>(); // Foydalanuvchi holati
    private final Map<Long, String> selectedProvinceMap = new HashMap<>(); // Tanlangan viloyat
    private final Map<Long, Boolean> contactReceivedMap = new HashMap<>(); // Kontakt qabul qilinganligini tekshirish

    // O‘zbekiston viloyatlari va Weatherbit API uchun moslashtirilgan nomlari
    private static final Map<String, String> PROVINCES = new HashMap<String, String>() {{
        put("Andijon", "Andijan");
        put("Buxoro", "Bukhara");
        put("Farg‘ona", "Fergana");
        put("Jizzax", "Jizzakh");
        put("Namangan", "Namangan");
        put("Navoiy", "Navoi");
        put("Qashqadaryo", "Qarshi");
        put("Samarqand", "Samarkand");
        put("Sirdaryo", "Guliston");
        put("Surxondaryo", "Termez");
        put("Toshkent viloyati", "Tashkent");
        put("Xorazm", "Urganch");
    }};


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String currentState = userStateMap.getOrDefault(chatId, "IDLE");
            boolean contactReceived = contactReceivedMap.getOrDefault(chatId, false);

            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                if (messageText.equals("/start")) {
                    sendContactRequestMessage(chatId);
                    userStateMap.put(chatId, "WAITING_FOR_CONTACT");
                    contactReceivedMap.put(chatId, false); // Kontakt hali qabul qilinmagan
                } else if (messageText.equals("Back")) {
                    handleBack(chatId, currentState, contactReceived);
                } else {
                    switch (currentState) {
                        case "SELECTING_PROVINCE":
                            if (PROVINCES.containsKey(messageText)) {
                                selectedProvinceMap.put(chatId, messageText);
                                sendForecastOptionMessage(chatId);
                                userStateMap.put(chatId, "SELECTING_FORECAST");
                            } else {
                                sendMessage(chatId, "❌ Noto‘g‘ri viloyat! Quyidagi ro‘yxatdan tanlang:");
                                sendProvinceSelectionMessage(chatId);
                            }
                            break;
                        case "SELECTING_FORECAST":
                            if (messageText.equals("1 kunlik")) {
                                String weatherData = getWeatherForecast(selectedProvinceMap.get(chatId), 1);
                                sendMessage(chatId, weatherData);
                                userStateMap.put(chatId, "SELECTING_PROVINCE");
                                sendProvinceSelectionMessage(chatId); // Kontakt so‘ralmaydi
                            } else if (messageText.equals("5 kunlik")) {
                                String weatherData = getWeatherForecast(selectedProvinceMap.get(chatId), 5);
                                sendMessage(chatId, weatherData);
                                userStateMap.put(chatId, "SELECTING_PROVINCE");
                                sendProvinceSelectionMessage(chatId); // Kontakt so‘ralmaydi
                            } else {
                                sendMessage(chatId, "❌ Iltimos, faqat '1 kunlik' yoki '5 kunlik'ni tanlang!");
                                sendForecastOptionMessage(chatId);
                            }
                            break;
                        case "WAITING_FOR_CONTACT":
                            sendMessage(chatId, "ℹ️ Iltimos, kontakt ma’lumotlaringizni ulashing!");
                            break;
                        default:
                            if (!contactReceived) {
                                sendMessage(chatId, "ℹ️ Botdan foydalanish uchun /start ni bosing va kontakt ulashing!");
                            } else {
                                sendProvinceSelectionMessage(chatId);
                                userStateMap.put(chatId, "SELECTING_PROVINCE");
                            }
                    }
                }
            } else if (update.getMessage().hasContact() && currentState.equals("WAITING_FOR_CONTACT")) {
                sendMessage(chatId, "✅ Kontakt qabul qilindi! Endi viloyatni tanlang:");
                sendProvinceSelectionMessage(chatId);
                System.out.println(update.getMessage().getFrom().getUserName()+"==> "+update.getMessage() );
                userStateMap.put(chatId, "SELECTING_PROVINCE");
                contactReceivedMap.put(chatId, true); // Kontakt qabul qilingan deb belgilandi
            }
        }
    }

    // Kontakt so‘rash uchun xabar (faqat /start da)
    private void sendContactRequestMessage(long chatId) {
        String welcomeText = "🌞 Salom! O‘zbekiston ob-havo botiga xush kelibsiz!\n" +
                "📞 Botdan foydalanish uchun iltimos, kontakt ma’lumotlaringizni ulashing.";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(welcomeText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton contactButton = new KeyboardButton("📞 Kontaktni ulashish");
        contactButton.setRequestContact(true);
        row1.add(contactButton);
        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Viloyat tanlash uchun xabar (Back olib tashlandi)
    private void sendProvinceSelectionMessage(long chatId) {
        String text = "🌍 Iltimos, viloyatni tanlang:";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        List<String> provinceNames = new ArrayList<>(PROVINCES.keySet());
        for (int i = 0; i < provinceNames.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.add(provinceNames.get(i));
            if (i + 1 < provinceNames.size()) {
                row.add(provinceNames.get(i + 1));
            }
            keyboard.add(row);
        }
        // Back tugmasi qo‘shilmaydi

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Prognoz turini tanlash uchun xabar (Back saqlanadi)
    private void sendForecastOptionMessage(long chatId) {
        String text = "🌦 Tanlangan viloyat: " + selectedProvinceMap.get(chatId) + "\n" +
                "📅 Ob-havo prognozini tanlang:";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("1 kunlik");
        row.add("5 kunlik");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Viloyat bo‘yicha ob-havo ma’lumotlarini olish
    private String getWeatherForecast(String province, int days) {
        String city = PROVINCES.get(province); // Weatherbit API uchun moslashtirilgan nom
        String url = days == 1 ?
                "https://api.weatherbit.io/v2.0/current?city=" + city + "&key=" + WEATHERBIT_API_KEY :
                "https://api.weatherbit.io/v2.0/forecast/daily?city=" + city + "&days=5&key=" + WEATHERBIT_API_KEY;

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode dataNode = days == 1 ? rootNode.path("data").get(0) : rootNode.path("data");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String currentTime = dateFormat.format(new Date());
                String cityName = rootNode.path("city_name").asText();

                if (days == 1) {
                    double temp = dataNode.path("temp").asDouble();
                    String weatherDescription = dataNode.path("weather").path("description").asText();
                    double windSpeed = dataNode.path("wind_spd").asDouble() * 3.6; // m/s dan km/soat ga

                    return String.format("📍 %s uchun ob-havo:\n" +
                                    "🗓️ Sana: %s\n" +
                                    "🌡 Harorat: %.0f°C\n" +
                                    "🌬 Shamol tezligi: %.0f km/soat\n" +
                                    "☁️ Ob-havo: %s\n" +
                                    "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _",
                            cityName, currentTime, temp, windSpeed, weatherDescription);
                } else {
                    StringBuilder forecast = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        JsonNode day = dataNode.get(i);
                        String date = day.path("valid_date").asText() + " " + currentTime.split(" ")[1]; // Sana + joriy vaqt
                        double temp = day.path("temp").asDouble();
                        String weatherDescription = day.path("weather").path("description").asText();
                        double windSpeed = day.path("wind_spd").asDouble() * 3.6; // m/s dan km/soat ga

                        forecast.append(String.format("📍 %s uchun ob-havo:\n" +
                                        "🗓️ Sana: %s\n" +
                                        "🌡 Harorat: %.0f°C\n" +
                                        "🌬 Shamol tezligi: %.0f km/soat\n" +
                                        "☁️ Ob-havo: %s\n" +
                                        "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n",
                                cityName, date, temp, windSpeed, weatherDescription));
                    }
                    return forecast.toString();
                }

            } else {
                return "❌ Ob-havo ma’lumotlarini olishda xatolik yuz berdi.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Xatolik: Ob-havo ma’lumotlari olinmadi.";
        }
    }

    // "Back" tugmasini boshqarish (faqat kun tanlash sahifasida ishlaydi)
    private void handleBack(long chatId, String currentState, boolean contactReceived) {
        switch (currentState) {
            case "WAITING_FOR_CONTACT":
                sendContactRequestMessage(chatId);
                userStateMap.put(chatId, "WAITING_FOR_CONTACT");
                break;
            case "SELECTING_FORECAST":
                sendProvinceSelectionMessage(chatId);
                userStateMap.put(chatId, "SELECTING_PROVINCE");
                break;
            default:
                if (!contactReceived) {
                    sendContactRequestMessage(chatId);
                    userStateMap.put(chatId, "WAITING_FOR_CONTACT");
                } else {
                    sendProvinceSelectionMessage(chatId);
                    userStateMap.put(chatId, "SELECTING_PROVINCE");
                }
        }
    }

    // Xabar yuborish uchun yordamchi metod
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}