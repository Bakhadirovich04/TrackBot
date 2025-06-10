package com.example.service.pages;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class HotDogPage {
    public static @NonNull List<KeyboardRow> getHotDogPage() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("\uD83C\uDF2D Classic Hot Dog ");
        row.add("\uD83C\uDF2E Chili Cheese Hot Dog");
        row.add("\uD83E\uDD6C Gourmet Veggie Hot Dog");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("\uD83D\uDED2Basket");
        keyboardRows.add(row);
        KeyboardRow row1 = new KeyboardRow();
        row1.add("↪️Exit");
        keyboardRows.add(row1);

        return keyboardRows;
    }
}
