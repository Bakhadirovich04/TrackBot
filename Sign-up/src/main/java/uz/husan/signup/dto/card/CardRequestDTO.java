package uz.husan.signup.dto.card;

import lombok.Data;

@Data
public class CardRequestDTO{
    private String cardNumber;
    private String password;
    private Double balance;
    private String cvv;
    private String expiryDate;
    private String cardType;

}
