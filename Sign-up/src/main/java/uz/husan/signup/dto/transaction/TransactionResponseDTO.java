package uz.husan.signup.dto.transaction;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class TransactionResponseDTO {
    private String fromCardNumber;
    private String invoiceNumber;
    private Double amount;
    private String companyName;
    private String cardHolderName;
    private LocalDateTime transactionDate;
}
