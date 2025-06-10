package com.example.service.contactUs;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class Contact {
    public static SendPhoto getSendPhoto(Long id) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img_4.png")));
        sendPhoto.setCaption("""
                ☕️ Mazzali taomlar!
                
                📊 Ish sifati ko'p yillik tajriba belgisi;
                
                💼 Samarali jamoa – muvaffaqiyat kaliti!
                
                📩 Biz bilan bog‘laning: @Respect571
                
                📍 Manzil: [Toshkent shahar]
                
                📞 Aloqa:  +998 94 004 44 44
                """);
        return sendPhoto;
    }
}
