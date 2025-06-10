package com.example;

import com.example.db.DataSource;
import com.example.entity.Expense;
import com.example.entity.UserData;
import com.example.entity.UserState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TrackBot extends TelegramLongPollingBot {


    @Override
    public String getBotUsername() {
        return DataSource.username;
    }
    @Override
    public String getBotToken() {
        return DataSource.token;
    }
    private static final String DATA_FILE = "Data.json";

    // Standart kategoriyalar ro'yxati
    private static final List<String> DEFAULT_CATEGORIES = new ArrayList<>() {{
        add("Oziq-ovqat");
        add("Transport");
        add("Ko'ngilochar");
        add("Kommunal xizmatlar");
        add("Boshqa");
    }};

    private Map<Long, UserData> userDataMap = new HashMap<>();
    private Map<Long, UserState> userStateMap = new HashMap<>();
    private Map<Long, String> userLanguageMap = new HashMap<>();
    private Map<Long, List<String>> userCategoriesMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public TrackBot() {
        loadDataFromFile();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            UserData userData = userDataMap.computeIfAbsent(chatId, k -> new UserData());
            UserState userState = userStateMap.computeIfAbsent(chatId, k -> new UserState());
            String language = userLanguageMap.computeIfAbsent(chatId, k -> "uz");
            List<String> userCategories = userCategoriesMap.computeIfAbsent(chatId, k -> new ArrayList<>(DEFAULT_CATEGORIES));

            if ("Back".equals(messageText)) {
                handleBack(chatId, userData, userState, language);
                return;
            }

            if (messageText.equals("/language")) {
                sendLanguageSelectionMessage(chatId);
                return;
            }

            switch (messageText) {
                case "/start":
                    sendWelcomeMessage(chatId, language);
                    userState.setCurrentState("IDLE");
                    break;
                case "/add":
                    sendCategorySelectionMessage(chatId, userCategories, language);
                    userState.setCurrentState("SELECTING_CATEGORY");
                    break;
                case "/view":
                    sendViewExpensesMessage(chatId, userData, language);
                    userState.setCurrentState("IDLE");
                    sendWelcomeMessage(chatId, language);
                    break;
                case "/stats":
                    sendStatsMessage(chatId, userData, language);
                    userState.setCurrentState("IDLE");
                    sendWelcomeMessage(chatId, language);
                    break;
                case "/budget":
                    sendBudgetMessage(chatId, userData, language);
                    userState.setCurrentState("IDLE");
                    sendWelcomeMessage(chatId, language);
                    break;
                case "/addcategory":
                    sendAddCategoryMessage(chatId, language);
                    userState.setCurrentState("ADDING_CATEGORY");
                    break;
                case "/deletecategory":
                    sendDeleteCategoryMessage(chatId, userCategories, language);
                    userState.setCurrentState("DELETING_CATEGORY");
                    break;
                case "🇺🇿 O'zbekcha":
                    userLanguageMap.put(chatId, "uz");
                    sendWelcomeMessage(chatId, "uz");
                    userState.setCurrentState("IDLE");
                    break;
                case "🇬🇧 English":
                    userLanguageMap.put(chatId, "en");
                    sendWelcomeMessage(chatId, "en");
                    userState.setCurrentState("IDLE");
                    break;
                default:
                    if (messageText.startsWith("/delete ")) {
                        handleDeleteExpense(chatId, messageText, userData, language);
                        userState.setCurrentState("IDLE");
                        sendWelcomeMessage(chatId, language);
                    } else {
                        handleUserInput(chatId, messageText, userData, userState, userCategories, language);
                    }
            }

            saveDataToFile();
        }
    }

    private void sendWelcomeMessage(long chatId, String language) {
        String welcomeText = language.equals("uz") ?
                "Salom! Men SpendyTrackBotman. Xarajatlaringizni kuzatishga yordam beraman.\n" +
                        "Quyidagi buyruqlardan foydalaning:\n" +
                        "/add - Xarajat qo'shish\n" +
                        "/view - Xarajatlarni ko'rish\n" +
                        "/stats - Statistika\n" +
                        "/budget - Byudjetni boshqarish\n" +
                        "/addcategory - Kategoriya qo'shish\n" +
                        "/deletecategory - Kategoriya o'chirish\n" +
                        "/language - Tilni o'zgartirish" :
                "Hello! I am SpendyTrackBot. I help you track your expenses.\n" +
                        "Use the following commands:\n" +
                        "/add - Add an expense\n" +
                        "/view - View expenses\n" +
                        "/stats - Statistics\n" +
                        "/budget - Manage budget\n" +
                        "/addcategory - Add a category\n" +
                        "/deletecategory - Delete a category\n" +
                        "/language - Change language";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(welcomeText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/add");
        row1.add("/view");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/stats");
        row2.add("/budget");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("/addcategory");
        row3.add("/deletecategory");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("/language");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendLanguageSelectionMessage(long chatId) {
        String text = "Iltimos, tilni tanlang / Please select a language:";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        row1.add("🇺🇿 O'zbekcha");
        row1.add("🇬🇧 English");

        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCategorySelectionMessage(long chatId, List<String> userCategories, String language) {
        String text = language.equals("uz") ?
                "Iltimos, xarajat kategoriyasini tanlang:" :
                "Please select an expense category:";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String category : userCategories) {
            KeyboardRow row = new KeyboardRow();
            row.add(category);
            keyboard.add(row);
        }
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow actionRow = new KeyboardRow();
        actionRow.add("Back");
        keyboard.add(actionRow);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAmountInputMessage(long chatId, String selectedCategory, String language) {
        String text = language.equals("uz") ?
                "Tanlangan kategoriya: " + selectedCategory + "\nIltimos, xarajat miqdorini kiriting (masalan, 50000):" :
                "Selected category: " + selectedCategory + "\nPlease enter the expense amount (e.g., 50000):";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow actionRow = new KeyboardRow();
        actionRow.add("Back");
        keyboard.add(actionRow);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDescriptionInputMessage(long chatId, String selectedCategory, double amount, String language) {
        String text = language.equals("uz") ?
                "Tanlangan kategoriya: " + selectedCategory + "\nMiqdor: " + amount + "\nIltimos, xarajat tavsifini kiriting (masalan, non):" :
                "Selected category: " + selectedCategory + "\nAmount: " + amount + "\nPlease enter the expense description (e.g., bread):";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow actionRow = new KeyboardRow();
        actionRow.add("Back");
        keyboard.add(actionRow);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAddCategoryMessage(long chatId, String language) {
        String text = language.equals("uz") ?
                "Iltimos, qo'shmoqchi bo'lgan kategoriya nomini kiriting (masalan, Kiyim):" :
                "Please enter the name of the category you want to add (e.g., Clothing):";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow actionRow = new KeyboardRow();
        actionRow.add("Back");
        keyboard.add(actionRow);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDeleteCategoryMessage(long chatId, List<String> userCategories, String language) {
        String text = language.equals("uz") ?
                "Iltimos, o'chirmoqchi bo'lgan kategoriyani tanlang:" :
                "Please select the category you want to delete:";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String category : userCategories) {
            KeyboardRow row = new KeyboardRow();
            row.add(category);
            keyboard.add(row);
        }
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow actionRow = new KeyboardRow();
        actionRow.add("Back");
        keyboard.add(actionRow);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleUserInput(long chatId, String messageText, UserData userData, UserState userState, List<String> userCategories, String language) {
        switch (userState.getCurrentState()) {
            case "SELECTING_CATEGORY":
                if (userCategories.contains(messageText)) {
                    userState.setSelectedCategory(messageText);
                    userState.setCurrentState("ENTERING_AMOUNT");
                    sendAmountInputMessage(chatId, messageText, language);
                } else {
                    String errorText = language.equals("uz") ?
                            "Noto'g'ri kategoriya. Iltimos, quyidagi kategoriyalardan birini tanlang:" :
                            "Invalid category. Please select one of the following categories:";
                    sendMessage(chatId, errorText);
                    sendCategorySelectionMessage(chatId, userCategories, language);
                }
                break;
            case "ENTERING_AMOUNT":
                try {
                    double amount = Double.parseDouble(messageText);
                    userState.setAmount(amount); // Miqdorni UserState’da saqlash
                    userState.setCurrentState("ENTERING_DESCRIPTION");
                    sendDescriptionInputMessage(chatId, userState.getSelectedCategory(), amount, language);
                } catch (NumberFormatException e) {
                    String errorText = language.equals("uz") ?
                            "Noto'g'ri miqdor. Iltimos, raqam kiriting (masalan, 50000):" :
                            "Invalid amount. Please enter a number (e.g., 50000):";
                    sendMessage(chatId, errorText);
                    sendAmountInputMessage(chatId, userState.getSelectedCategory(), language);
                }
                break;
            case "ENTERING_DESCRIPTION":
                String description = messageText;
                double amount = userState.getAmount(); // UserState’dan olish
                if (userData.getBudget() >= amount) {
                    Expense expense = new Expense(userState.getSelectedCategory(), amount, description);
                    userData.addExpense(expense);
                    userData.setBudget(userData.getBudget() - amount);
                    userState.setCurrentState("IDLE");
                    String successText = language.equals("uz") ?
                            "Xarajat muvaffaqiyatli qo'shildi!\nKategoriya: " + userState.getSelectedCategory() + "\nMiqdor: " + amount + "\nTavsif: " + description + "\nQolgan balans: " + userData.getBudget() :
                            "Expense added successfully!\nCategory: " + userState.getSelectedCategory() + "\nAmount: " + amount + "\nDescription: " + description + "\nRemaining balance: " + userData.getBudget();
                    sendMessage(chatId, successText);
                    sendWelcomeMessage(chatId, language);
                } else {
                    userState.setCurrentState("IDLE");
                    String errorText = language.equals("uz") ?
                            "Balansingiz yetarli emas! Hozirgi balans: " + userData.getBudget() + "\nIltimos, byudjetni yangilash uchun /budget miqdor deb yozing (masalan, /budget 1000000)." :
                            "Insufficient balance! Current balance: " + userData.getBudget() + "\nPlease update your budget by typing /budget amount (e.g., /budget 1000000).";
                    sendMessage(chatId, errorText);
                    sendWelcomeMessage(chatId, language);
                }
                break;
            case "ADDING_CATEGORY":
                if (!userCategories.contains(messageText)) {
                    userCategories.add(messageText);
                    String successText = language.equals("uz") ?
                            "Kategoriya muvaffaqiyatli qo'shildi: " + messageText :
                            "Category added successfully: " + messageText;
                    sendMessage(chatId, successText);
                } else {
                    String errorText = language.equals("uz") ?
                            "Bu kategoriya allaqachon mavjud: " + messageText :
                            "This category already exists: " + messageText;
                    sendMessage(chatId, errorText);
                }
                userState.setCurrentState("IDLE");
                sendWelcomeMessage(chatId, language);
                break;
            case "DELETING_CATEGORY":
                if (userCategories.contains(messageText)) {
                    userCategories.remove(messageText);
                    String successText = language.equals("uz") ?
                            "Kategoriya muvaffaqiyatli o'chirildi: " + messageText :
                            "Category deleted successfully: " + messageText;
                    sendMessage(chatId, successText);
                } else {
                    String errorText = language.equals("uz") ?
                            "Bu kategoriya mavjud emas: " + messageText :
                            "This category does not exist: " + messageText;
                    sendMessage(chatId, errorText);
                }
                userState.setCurrentState("IDLE");
                sendWelcomeMessage(chatId, language);
                break;
            default:
                if (messageText.startsWith("/budget ")) {
                    try {
                        double newBudget = Double.parseDouble(messageText.split(" ")[1]);
                        userData.setBudget(newBudget);
                        String successText = language.equals("uz") ?
                                "Yangi byudjet muvaffaqiyatli belgilandi: " + newBudget :
                                "New budget set successfully: " + newBudget;
                        sendMessage(chatId, successText);
                        sendWelcomeMessage(chatId, language);
                    } catch (Exception e) {
                        String errorText = language.equals("uz") ?
                                "Noto'g'ri format. Iltimos, /budget miqdor deb yozing." :
                                "Invalid format. Please type /budget amount.";
                        sendMessage(chatId, errorText);
                        sendWelcomeMessage(chatId, language);
                    }
                }
        }
    }

    private void handleBack(long chatId, UserData userData, UserState userState, String language) {
        List<String> userCategories = userCategoriesMap.getOrDefault(chatId, new ArrayList<>(DEFAULT_CATEGORIES));
        switch (userState.getCurrentState()) {
            case "SELECTING_CATEGORY":
            case "ADDING_CATEGORY":
            case "DELETING_CATEGORY":
                userState.setCurrentState("IDLE");
                sendWelcomeMessage(chatId, language);
                break;
            case "ENTERING_AMOUNT":
                userState.setCurrentState("SELECTING_CATEGORY");
                sendCategorySelectionMessage(chatId, userCategories, language);
                break;
            case "ENTERING_DESCRIPTION":
                userState.setCurrentState("ENTERING_AMOUNT");
                sendAmountInputMessage(chatId, userState.getSelectedCategory(), language);
                break;
            default:
                userState.setCurrentState("IDLE");
                sendWelcomeMessage(chatId, language);
        }
    }

    private void sendViewExpensesMessage(long chatId, UserData userData, String language) {
        if (userData.getExpenses().isEmpty()) {
            String text = language.equals("uz") ?
                    "Sizda hali xarajatlar yo'q." :
                    "You have no expenses yet.";
            sendMessage(chatId, text);
        } else {
            StringBuilder response = new StringBuilder(language.equals("uz") ?
                    "Sizning xarajatlaringiz (o'chirish uchun /delete <raqam>):\n" :
                    "Your expenses (to delete, type /delete <number>):\n");
            int index = 1;
            for (Expense expense : userData.getExpenses()) {
                response.append(String.format("%d. %s: %s, %.2f, %s\n",
                        index, language.equals("uz") ? "Kategoriya" : "Category",
                        expense.getCategory(), expense.getAmount(), expense.getDescription()));
                index++;
            }
            sendMessage(chatId, response.toString());
        }
    }

    private void sendStatsMessage(long chatId, UserData userData, String language) {
        if (userData.getExpenses().isEmpty()) {
            String text = language.equals("uz") ?
                    "Statistika uchun hali xarajatlar yo'q." :
                    "No expenses yet for statistics.";
            sendMessage(chatId, text);
        } else {
            double total = userData.getExpenses().stream().mapToDouble(Expense::getAmount).sum();
            Map<String, Double> categoryTotals = new HashMap<>();
            for (Expense expense : userData.getExpenses()) {
                categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            }

            StringBuilder response = new StringBuilder(language.equals("uz") ? "Statistika:\n" : "Statistics:\n");
            response.append(String.format(language.equals("uz") ? "Jami xarajat: %.2f\n" : "Total expenses: %.2f\n", total));
            response.append(language.equals("uz") ? "Kategoriyalar bo'yicha:\n" : "By category:\n");
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                response.append(String.format("%s: %.2f\n", entry.getKey(), entry.getValue()));
            }
            response.append(String.format(language.equals("uz") ? "Qolgan balans: %.2f" : "Remaining balance: %.2f", userData.getBudget()));
            sendMessage(chatId, response.toString());
        }
    }

    private void sendBudgetMessage(long chatId, UserData userData, String language) {
        String text = language.equals("uz") ?
                "Hozirgi byudjet: " + userData.getBudget() + "\n" +
                        "Yangi byudjetni belgilash uchun /budget miqdor deb yozing.\n" +
                        "Masalan: /budget 1000000" :
                "Current budget: " + userData.getBudget() + "\n" +
                        "To set a new budget, type /budget amount.\n" +
                        "For example: /budget 1000000";
        sendMessage(chatId, text);
    }

    private void handleDeleteExpense(long chatId, String messageText, UserData userData, String language) {
        try {
            int index = Integer.parseInt(messageText.split(" ")[1]) - 1;
            if (index >= 0 && index < userData.getExpenses().size()) {
                Expense deletedExpense = userData.getExpenses().remove(index);
                userData.setBudget(userData.getBudget() + deletedExpense.getAmount());
                String text = language.equals("uz") ?
                        "Xarajat muvaffaqiyatli o'chirildi! Qolgan balans: " + userData.getBudget() :
                        "Expense deleted successfully! Remaining balance: " + userData.getBudget();
                sendMessage(chatId, text);
            } else {
                String text = language.equals("uz") ?
                        "Noto'g'ri raqam. Iltimos, xarajatlar ro'yxatidagi to'g'ri raqamni kiriting." :
                        "Invalid number. Please enter a valid number from the expenses list.";
                sendMessage(chatId, text);
            }
        } catch (Exception e) {
            String text = language.equals("uz") ?
                    "Noto'g'ri format. Iltimos, /delete <raqam> deb yozing." :
                    "Invalid format. Please type /delete <number>.";
            sendMessage(chatId, text);
        }
    }

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

    private void loadDataFromFile() {
        lock.readLock().lock();
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                Map<Long, UserData> loadedData = objectMapper.readValue(file, objectMapper.getTypeFactory()
                        .constructMapType(Map.class, Long.class, UserData.class));
                userDataMap.putAll(loadedData);
            }
        } catch (IOException e) {
            System.err.println("Fayldan ma’lumot o‘qishda xato: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

    private void saveDataToFile() {
        lock.writeLock().lock();
        try {
            objectMapper.writeValue(new File(DATA_FILE), userDataMap);
        } catch (IOException e) {
            System.err.println("Faylga saqlashda xato: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }
}