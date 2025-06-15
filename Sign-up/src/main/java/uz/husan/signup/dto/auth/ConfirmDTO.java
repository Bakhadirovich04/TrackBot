package uz.husan.signup.dto.auth;

import lombok.Data;

@Data
public class ConfirmDTO {
    private String code;
    private String email;
}
