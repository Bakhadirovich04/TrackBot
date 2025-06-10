package com.example.service.pages;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainPage {
    public static @NonNull List<KeyboardRow> getMainPage(Long id, Long admin) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("🏠Menu");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("⚙\uFE0FSettings");
        row.add("🔢Contact us");
        keyboardRows.add(row);
        if(Objects.equals(id,admin)) {
            row = new KeyboardRow();
            row.add("✉\uFE0FSend Message");
            keyboardRows.add(row);
        }
        return keyboardRows;
    }
}
