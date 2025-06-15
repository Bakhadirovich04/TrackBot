package uz.husan.signup.dto.company;

import lombok.Data;

@Data
public class CompanyRequestDTO {
    private String companyName;
    private String address;
    private String phoneNumber;
    private Double balance;
}
