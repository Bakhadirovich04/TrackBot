package uz.husan.signup.dto.card;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardResponseDTO {
    private Long id;
    private String cardNumber;
    private String cardHolderName;
    private String cardType;
    private String password;
    private Double balance;
    private String cvv;
    private String expiryDate;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;
}
