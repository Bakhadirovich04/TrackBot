package uz.husan.signup.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.husan.signup.dto.card.CardRequestDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.entity.Card;
import uz.husan.signup.dto.card.CardResponseDTO;
import uz.husan.signup.entity.User;
import uz.husan.signup.repository.CardRepository;
import uz.husan.signup.repository.UserRepository;
import uz.husan.signup.service.CardService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    final CardRepository cardRepository;
    final UserRepository userRepository;

    @Override
    public ResponseMessage createCard(CardRequestDTO cardRequestDTO) {
        User user = null;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("You are not logged in")
                    .build();
        }

        if (user == null) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("Sorry, you are not recognized in the system.")
                    .build();
        }
        Boolean b = cardRepository.existsByCardNumber(cardRequestDTO.getCardNumber());
        if (b) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("This card already exists")
                    .build();
        }

        Card card = new Card();
        card.setCardNumber(cardRequestDTO.getCardNumber());
        card.setPassword(cardRequestDTO.getPassword());
        card.setCardHolder(user);
        card.setBalance(cardRequestDTO.getBalance());
        card.setCardType(cardRequestDTO.getCardType());
        card.setCvv(cardRequestDTO.getCvv());
        card.setExpiryDate(cardRequestDTO.getExpiryDate());
        card.setRegisterDate(LocalDateTime.now());
        List<Card> userCards = user.getCard();
        userCards.add(card);
        user.setBalance(user.getBalance() + card.getBalance());
        cardRepository.save(card);
        user.setCard(userCards);
        userRepository.save(user);
        CardResponseDTO cardResponseDTO = getCard(card);
        return ResponseMessage.builder().success(true).data(cardResponseDTO).message("Card succesfully added...").build();

    }
    public CardResponseDTO getCard(Card card) {
        CardResponseDTO cardResponseDTO = new CardResponseDTO();
        cardResponseDTO.setId(card.getId());
        cardResponseDTO.setCardNumber(card.getCardNumber());
        cardResponseDTO.setCardHolderName(card.getCardHolder().getFullName().toUpperCase());
        cardResponseDTO.setCardType(card.getCardType());
        cardResponseDTO.setPassword(card.getPassword());
        cardResponseDTO.setBalance(card.getBalance());
        cardResponseDTO.setCvv(card.getCvv());
        cardResponseDTO.setExpiryDate(card.getExpiryDate());
        cardResponseDTO.setRegisterDate(card.getRegisterDate());
        cardResponseDTO.setUpdateDate(card.getUpdateDate());
        return cardResponseDTO;
    }



    @Override
    public ResponseMessage getAllCards(Integer page, Integer size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<CardResponseDTO> all = cardRepository
                .findAllByCardsEm(user.getEmail(), pageRequest).map(this::getCard );
        if (all.isEmpty()){
            return ResponseMessage.builder().success(false).message("Cards no such exists").data(null).build();
        }
        return ResponseMessage
                .builder()
                .message("Cards fetched successfully")
                .success(true)
                .data(all)
                .build();
    }
    @Override
    public ResponseMessage updateCard(String card_number, String last_pin, String new_pin, Double balance12) {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Card> optionalCard = cardRepository.findCardByCardNumber(card_number);
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            if (card.getCardHolder().getId().equals(user.getId())) {
                if (card.getPassword().equals(last_pin)) {
                    if(balance12<0){
                        return ResponseMessage.builder()
                                .success(false)
                                .data(null)
                                .message("Balance cannot be negative")
                                .build();
                    }
                    LocalDateTime time = LocalDateTime.now();
                    cardRepository.updateCard(card.getCardNumber(), new_pin, balance12,time);
                    user.setBalance(user.getBalance() + balance12);
                    userRepository.save(user);
                    Optional<Card> card1 = cardRepository.findCardByCardNumber(card_number);
                    if (card1.isEmpty()) {
                        return ResponseMessage.builder()
                                .success(false)
                                .data(null)
                                .message("Card not found after update")
                                .build();
                    }
                    CardResponseDTO cardResponseDTO = getCard(card1.get());
                    return ResponseMessage.builder()
                            .success(true)
                            .data(cardResponseDTO)
                            .message("Card updated successfully")
                            .build();
                } else {
                    return ResponseMessage.builder()
                            .success(false)
                            .data(null)
                            .message("Incorrect last pin")
                            .build();
                }
            } else {
                return ResponseMessage.builder()
                        .success(false)
                        .data(null)
                        .message("Unfortunately, you cannot update this card.")
                        .build();
            }
        } else {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("Card not found")
                    .build();

        }

    }

    @Override
    public ResponseMessage deleteCard(String card_number) {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Card> optionalCard = cardRepository.findCardByCardNumber(card_number);
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            if (card.getCardHolder().getId().equals(user.getId())) {
                user.setBalance(user.getBalance() - card.getBalance());
                userRepository.save(user);
                cardRepository.deleteByNumber(card.getCardNumber());
                return ResponseMessage.builder()
                        .success(true)
                        .data(null)
                        .message("Card deleted successfully")
                        .build();
            } else {
                return ResponseMessage.builder()
                        .success(false)
                        .data(null)
                        .message("Unfortunately, you cannot delete this card.")
                        .build();
            }
        } else {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("Card not found")
                    .build();
        }
    }
}

