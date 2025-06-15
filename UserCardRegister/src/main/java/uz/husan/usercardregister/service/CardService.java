package uz.husan.usercardregister.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.husan.usercardregister.dto.ResponseMessage;
import uz.husan.usercardregister.entity.Card;
import uz.husan.usercardregister.entity.Users;
import uz.husan.usercardregister.repository.CardRepository;
import uz.husan.usercardregister.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public ResponseMessage createCard(Long userId, Card card) {
        List<Card> cards = cardRepository.findAll();
        Optional<Users> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseMessage.builder()
                    .status(false)
                    .data(null)
                    .message("User not found")
                    .build();
        }
        for (Card card1 : cards) {
            if (card1.getCardNumber().equals(card.getCardNumber())) {
                return ResponseMessage.builder()
                        .status(false)
                        .data(null)
                        .message("This card already exists")
                        .build();
            }
        }
        Card card1 = new Card();
        card1.setCardHolder(user.get());
        card1.setCardNumber(card.getCardNumber());
        card1.setCardName(card.getCardName());
        card1.setCvv(card.getCvv());
        card1.setBalance(card.getBalance());
        Users user1 = user.get();
        List<Card> userCards = user1.getCard();
        userCards.add(card1);
        cardRepository.save(card1);
        user1.setCard(userCards);
        return ResponseMessage.builder().status(true).data(userRepository.save(user1)).message("Card succesfully added...").build();

    }

    public ResponseMessage getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return ResponseMessage.builder()
                .status(true)

                .data(cards)
                .message("Card list retrieved successfully")
                .build();
    }

    public ResponseMessage updateCard(Long id, Long userId, Card card) {
        Optional<Users> user = userRepository.findById(userId);
        Optional<Card> card1 = cardRepository.findById(id);
        if (!user.isPresent() || !card1.isPresent()) {
            return ResponseMessage.builder()
                    .status(false)
                    .data(null)
                    .message("User or card not found")
                    .build();
        }
        if (cardRepository.existsByCardNumberAndIdNot(card.getCardNumber(), id)) {
            throw new RuntimeException("Card with this number already exists");
        }
        Card existingCard = card1.get();
        existingCard.setCardHolder(user.get());
        existingCard.setCardNumber(card.getCardNumber());
        existingCard.setCardName(card.getCardName());
        existingCard.setCvv(card.getCvv());
        existingCard.setBalance(card.getBalance());
        existingCard.setUpdateDate(LocalDateTime.now());
        return ResponseMessage.builder().status(true).data(cardRepository.save(existingCard)).message("Card succesfully updated...").build();

    }

    public ResponseMessage deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            return ResponseMessage.builder().status(false).data(null).message("card not found").build();
        }
        cardRepository.deleteById(id);
        return ResponseMessage.builder().status(false).data(null).message("Card deleted....").build();
    }
}

