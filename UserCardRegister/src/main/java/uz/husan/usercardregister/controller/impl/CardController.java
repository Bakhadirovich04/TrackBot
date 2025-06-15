package uz.husan.usercardregister.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.usercardregister.controller.CardControllerInterfeys;
import uz.husan.usercardregister.entity.Card;
import uz.husan.usercardregister.service.CardService;

@RestController
@RequiredArgsConstructor
public class CardController implements CardControllerInterfeys {
    final CardService cardService;
    @Override
    public ResponseEntity<?> getAllCards() {
       return ResponseEntity.ok(cardService.getAllCards());
    }

    @Override
    public ResponseEntity<?> createCard(Long id,Card card) {
        return ResponseEntity.ok(cardService.createCard(id,card));
    }

    @Override
    public ResponseEntity<?> updateCard(Long id, Long userId, Card card) {
        return ResponseEntity.ok(cardService.updateCard(id, userId, card));
    }

    @Override
    public ResponseEntity<?> deleteCard(Long id) {
        return ResponseEntity.ok(cardService.deleteCard(id));

    }
}
