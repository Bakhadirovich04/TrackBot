package com.example.service.pages;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MenuPage {
    public static @NonNull List<KeyboardRow> getMenuPage() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("🌭Hot-Dog");
        row.add("🍔Burger");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("☕Coffee");
        row.add("🧋Bubble Tea");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("↪️Exit");
        keyboardRows.add(row);
        return keyboardRows;
    }
}
