package uz.husan.signup.dto.invoice;

import lombok.Data;

@Data
public class InvoiceUpdateDTO {
    private Double amount;
    private String status;
}
