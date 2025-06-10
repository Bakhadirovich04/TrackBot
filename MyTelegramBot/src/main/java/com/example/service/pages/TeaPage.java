package com.example.service.pages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class TeaPage {
    public static List<KeyboardRow> getTeaPage() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Green Tea");
        row.add("Black Tea");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("Herbal Tea");
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
