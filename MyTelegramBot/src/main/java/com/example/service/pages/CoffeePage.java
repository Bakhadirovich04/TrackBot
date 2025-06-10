package com.example.service.pages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class CoffeePage {

    public static List<KeyboardRow> getCoffeePage() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("\uD83C\uDF6BMocha Coffee");
        row.add("\uD83E\uDD5B Cappuccino");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("\uD83E\uDDCA Iced Coffee");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("\uD83D\uDED2Basket");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("↪️Exit");
        keyboardRows.add(row);
        return keyboardRows;
    }
}
