package uz.husan.signup.dto.invoice;

import lombok.Data;

@Data
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private Double amount;
    private String status;
    private String companyName;
}
