package uz.husan.signup.service;

import org.springframework.stereotype.Service;
import uz.husan.signup.dto.card.CardRequestDTO;
import uz.husan.signup.entity.ResponseMessage;

@Service
public interface CardService {
    ResponseMessage getAllCards(Integer page, Integer size);

    ResponseMessage createCard(CardRequestDTO cardRequestDTO);

    ResponseMessage updateCard(String card_number, String last_pin, String new_pin, Double balance);

    ResponseMessage deleteCard(String card_number);
}
