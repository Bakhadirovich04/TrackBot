package uz.husan.signup.dto.transaction;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private String invoiceNumber;
    private String fromCardNumber;
    private Double amount;
}
