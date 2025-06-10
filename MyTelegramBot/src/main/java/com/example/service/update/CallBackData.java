package com.example.service.update;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallBackData {
    public static void performCallBackData(Update update,HashMap<Long, Integer> state,HashMap<Long, List<String>> basket,HashMap<Long, HashMap<Integer, Integer>> orders,int amount) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        Integer i = state.get(chatId);
        if (update.getCallbackQuery().getData().equals("basket")) {
            if(basket==null){
                sendMessage.setText("Sizning savatingiz hozircha bo'sh");
            }
            else {
                HashMap<Integer, Integer> userOrder = orders.get(chatId);
                basket.putIfAbsent(chatId, new ArrayList<>());
                List<String> strings = basket.get(chatId);
                strings.add("you have ordered " + userOrder.get(i) + " " + (i == 4 ? "✅ Non Burger" : i == 5 ?
                        "✅ Tandir Burger" : i == 6 ? "✅ Samarqand Burger" :
                        i == 7 ? "\uD83C\uDF2D Classic Hot Dog" : i == 8 ? "\uD83C\uDF2E Chili Cheese Hot Dog" :
                                i == 9 ? "\uD83E\uDD6C Gourmet Veggie Hot Dog" : i == 10 ? "\uD83C\uDF6BMocha Coffee" : i == 11 ? "\uD83E\uDD5B Cappuccino" : i == 12 ? "\uD83E\uDDCA Iced Coffee"
                                        : i == 13 ? "Green Tea" : i == 14 ? "Black Tea" : i == 15 ? "Herbal Tea" : ""));
                return;
            }
        }
        switch (i) {
            case 4 -> {
                performOrder(chatId, update, 4,orders,amount);
            }

            case 5 -> {
                performOrder(chatId, update, 5,orders,amount);
            }
            case 6 -> {
                performOrder(chatId, update, 6,orders,amount);
            }
            case 7 -> {
                performOrder(chatId, update, 7,orders,amount);
            }
            case 8 -> {
                performOrder(chatId, update, 8,orders,amount);
            }
            case 9 -> {
                performOrder(chatId, update, 9,orders,amount);
            }
            case 10 -> {
                performOrder(chatId, update, 10,orders,amount);
            }
            case 11 -> {
                performOrder(chatId, update, 11,orders,amount);
            }
            case 12 -> {
                performOrder(chatId, update, 12,orders,amount);
            }
            case 13 -> {
                performOrder(chatId, update, 13,orders,amount);
            }
            case 14 -> {
                performOrder(chatId, update, 14,orders,amount);
            }
            case 15 -> {
                performOrder(chatId, update, 15,orders,amount);
            }

            default -> {
                sendMessage.setText("⚠\uFE0F Oops! That’s not a valid command. ");
            }
        }

    }
    public static void performOrder(Long chatId, Update update, Integer itemNumber, HashMap<Long, HashMap<Integer, Integer>> orders,int amount) {

        if (update.getCallbackQuery().getData().equals("plus")) {
            amount = 1;
        }
        if (update.getCallbackQuery().getData().equals("minus")) {
            amount = -1;
        }
        orders.putIfAbsent(chatId, new HashMap<Integer, Integer>());
        HashMap<Integer, Integer> userOrder = orders.get(chatId);


        if (userOrder.containsKey(itemNumber)) {
            userOrder.put(itemNumber, userOrder.get(itemNumber) + amount);
        } else {
            if (amount > 0) {
                userOrder.put(itemNumber, 1);
            }
        }
    }
}
