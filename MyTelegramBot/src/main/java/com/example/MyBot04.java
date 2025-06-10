package com.example;


import com.example.db.DataSource;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

import static com.example.service.contactUs.Contact.getSendPhoto;
import static com.example.service.pages.BurgerPage.getBurgerPage;
import static com.example.service.pages.CoffeePage.getCoffeePage;
import static com.example.service.pages.HotDogPage.getHotDogPage;
import static com.example.service.pages.MainPage.getMainPage;
import static com.example.service.pages.MenuPage.getMenuPage;
import static com.example.service.pages.TeaPage.getTeaPage;
import static com.example.service.update.CallBackData.performCallBackData;

public class MyBot04 extends TelegramLongPollingBot {
    static Long admin=7603874088l;
    int a;
    private static final Map<Long, List<List<KeyboardRow>>> userHistory = new HashMap<>();
    HashMap<Long, HashMap<Integer, Integer>> orders = new HashMap<>();
    HashMap<Long, Integer> state = new HashMap<>();
    HashMap<Long, List<String>> basket = new HashMap<>();
    static Set<Long> users = new HashSet<>();
    static int amount = 0;
    @SneakyThrows

    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            performCallBackData(update,state,basket,orders,amount);
        }
        if (update.hasMessage()) {
           performMessage(update);
        }
    }
        public void performMessage(Update update) throws TelegramApiException {
        Long id = update.getMessage().getChatId();
        users.add(id);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        if (update.getMessage().hasContact()) {
            Contact contact = update.getMessage().getContact();
            System.out.println(update.getMessage().getChat().getUserName() + "=>" + contact.getPhoneNumber());
            sendMessage.setText("Thank you " + contact.getFirstName());
            ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
            replyKeyboard.setResizeKeyboard(true);
            replyKeyboard.setKeyboard(getMainPage(id,admin));
            sendMessage.setReplyMarkup(replyKeyboard);
            execute(sendMessage);
            return;
        }
        String text=update.getMessage().getText();
        if(a==73){
            sendMessageToAll(text,id);
            text="✉\uFE0FSms Chat";
            a=0;
        }
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setResizeKeyboard(true);
        userHistory.putIfAbsent(id, new ArrayList<>());
        switch (text) {
            case "/start" -> {
                List<KeyboardRow> keyboardRows = new ArrayList<>();
                KeyboardButton button = new KeyboardButton();
                KeyboardRow row = new KeyboardRow();
                button.setText("📱Share Contact");
                button.setRequestContact(true);
                row.add(button);
                keyboardRows.add(row);
                replyKeyboard.setKeyboard(keyboardRows);
                sendMessage.setText("Hi");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "✉\uFE0FSend Message" -> {
                sendMessage.setText("🔻Please write.....");
                a=73;
            }
            case "🏠Menu" -> {
                List<KeyboardRow> menuPage = getMenuPage();
                savePage(id, menuPage);
                replyKeyboard.setKeyboard(getMenuPage());
                sendMessage.setText("Choose one of our products👇👇");
                sendMessage.setReplyMarkup(replyKeyboard);

            }
            case "↪\uFE0FExit" -> {

                List<KeyboardRow> previousPage = getPreviousPage(id);
                replyKeyboard.setKeyboard(previousPage);
                sendMessage.setText("Succes ✅");
                sendMessage.setReplyMarkup(replyKeyboard);

            }
            case "🔢Contact us" -> {
                SendPhoto sendPhoto = getSendPhoto(id);
                execute(sendPhoto);

            }
            case "☕Coffee" -> {
                SendPhoto sendPhoto = new SendPhoto();
                List<KeyboardRow> coffeePage = getCoffeePage();
                savePage(id, coffeePage);
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_20.png")));
                sendPhoto.setCaption("Coffees");
                execute(sendPhoto);
                replyKeyboard.setKeyboard(getCoffeePage());
                sendMessage.setText("⬆️Select");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "🧋Bubble Tea" -> {
                SendPhoto sendPhoto = new SendPhoto();
                List<KeyboardRow> teaPage = getTeaPage();
                savePage(id, teaPage);
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_5.png")));
                sendPhoto.setCaption("Teas");
                execute(sendPhoto);
                replyKeyboard.setKeyboard(getTeaPage());
                sendMessage.setText("⬆️Select");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "🌭Hot-Dog" -> {
                SendPhoto sendPhoto = new SendPhoto();
                List<KeyboardRow> hotDogPage = getHotDogPage();
                savePage(id, hotDogPage);
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_3.png")));
                sendPhoto.setCaption("Hot Dogs");
                execute(sendPhoto);
                replyKeyboard.setKeyboard(getHotDogPage());
                sendMessage.setText("⬆️Select");
                sendMessage.setReplyMarkup(replyKeyboard);

            }
            case "🍔Burger" -> {
                SendPhoto sendPhoto = new SendPhoto();
                List<KeyboardRow> burgerPage = getBurgerPage();
                savePage(id, burgerPage);
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_1.png")));
                sendPhoto.setCaption("Buregers");
                execute(sendPhoto);
                replyKeyboard.setKeyboard(getBurgerPage());
                sendMessage.setText("⬆️Select");
                sendMessage.setReplyMarkup(replyKeyboard);

            }
            case "✅ Non Burger" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_11.png")));
                sendPhoto.setCaption("✅ Non Burger!\n25.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 4);
            }
            case "✅ Tandir Burger" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_10.png")));
                sendPhoto.setCaption("✅ Tandir Burger!\n35.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 5);
            }
            case "✅ Samarqand Burger" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_9.png")));
                sendPhoto.setCaption("✅ Samarqand Burger!\n30.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 6);
            }
            case "\uD83D\uDED2Basket" -> {
                state.put(id, 3);
                StringBuilder sb = new StringBuilder();
                for (String s : basket.get(id)) {
                    sb.append(s).append("\n");
                }

                sendMessage.setText(sb.toString());
            }
            case "\uD83C\uDF2D Classic Hot Dog" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_8.png")));
                sendPhoto.setCaption("\uD83C\uDF2D Classic Hot Dog!\n28.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 7);
            }
            case "\uD83C\uDF2E Chili Cheese Hot Dog" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_7.png")));
                sendPhoto.setCaption("\uD83C\uDF2E Chili Cheese Hot Dog!\n24.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 8);
            }
            case "\uD83E\uDD6C Gourmet Veggie Hot Dog" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_6.png")));
                sendPhoto.setCaption("\uD83E\uDD6C Gourmet Veggie Hot Dog!\n20.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 9);
            }
            case "\uD83C\uDF6BMocha Coffee" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_15.png")));
                sendPhoto.setCaption("\uD83C\uDF6BMocha Coffee\n18.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 10);
            }
            case "\uD83E\uDD5B Cappuccino" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_13.png")));
                sendPhoto.setCaption("\uD83E\uDD5B Cappuccino\n20.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 11);
            }
            case "\uD83E\uDDCA Iced Coffee" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_14.png")));
                sendPhoto.setCaption("\uD83E\uDDCA Iced Coffee\n15.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 12);
            }
            case "Green Tea" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_19.png")));
                sendPhoto.setCaption("Green Tea\n15.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 13);
            }
            case "Black Tea" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_17.png")));
                sendPhoto.setCaption("Black Tea\n8.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 14);
            }
            case "Herbal Tea" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(id);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_18.png")));
                sendPhoto.setCaption("Herbal Tea\n12.000 so'm\n🔽Enter amount");
                sendPhoto.setReplyMarkup(getInlineKeyboard());
                execute(sendPhoto);
                state.put(id, 15);
            }
            case "✉\uFE0FSms Chat"->{
                sendMessage.setText("\uD83D\uDCE5Sent message✅");
            }
            default -> {
                sendMessage.setText("⚠\uFE0F Oops! That’s not a valid command. ");
            }

        }
        execute(sendMessage);
        System.out.println(update.getMessage().getChat().getUserName() + " wrote => " + text);
    }

        private static void savePage(Long id,List<KeyboardRow> page) {
        userHistory.get(id).add(page);
    }
        @SneakyThrows
        public void sendMessageToAll(String text, Long id) {
        for (Long user : users) {
            if(!Objects.equals(user,id)) {
                SendMessage message = new SendMessage();
                message.setChatId(user);
                message.setText(text);
                execute(message);
            }
        }
    }
        public static List<KeyboardRow> getPreviousPage(Long id) {
        List<List<KeyboardRow>> history = userHistory.get(id);
        if (history.size() > 1) {
            history.remove(history.size() - 1);
            return history.get(history.size() - 1);
        }
        return getMainPage(id,admin);

    }


    public static ReplyKeyboard getInlineKeyboard() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listButtons = new ArrayList<>();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("-");
        button1.setCallbackData("minus");
        buttons.add(button1);
        InlineKeyboardButton button2 = new InlineKeyboardButton(String.valueOf(amount));
        button2.setCallbackData("qiymat");
        buttons.add(button2);
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("+");
        button3.setCallbackData("plus");
        buttons.add(button3);
        listButtons.add(buttons);

        buttons = new ArrayList<>();
        button1 = new InlineKeyboardButton();
        button1.setText("\uD83D\uDED2To basket");
        button1.setCallbackData("basket");
        buttons.add(button1);
        listButtons.add(buttons);


        markup.setKeyboard(listButtons);
        return markup;
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
