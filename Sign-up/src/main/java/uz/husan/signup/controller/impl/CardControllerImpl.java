package uz.husan.signup.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.signup.controller.CardController;
import uz.husan.signup.dto.card.CardRequestDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.service.CardService;

@RestController
@RequiredArgsConstructor
public class CardControllerImpl implements CardController {
    final CardService cardService;

    @Override
    public ResponseEntity<?> getAllCards(Integer page, Integer size) {
        ResponseMessage responseMessage= cardService.getAllCards(page, size);
       return ResponseEntity.status(responseMessage.getSuccess()?200:400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> createCard(CardRequestDTO cardRequestDTO) {
       ResponseMessage responseMessage = cardService.createCard(cardRequestDTO);
         return ResponseEntity.status(responseMessage.getSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> updateCard(String card_number, String last_pin, String new_pin, Double balance) {
        ResponseMessage responseMessage = cardService.updateCard(card_number, last_pin, new_pin, balance);
        return ResponseEntity.status(responseMessage.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> deleteCard(String card_number) {
        ResponseMessage responseMessage = cardService.deleteCard(card_number);
        return ResponseEntity.status(responseMessage.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseMessage);

    }

}
