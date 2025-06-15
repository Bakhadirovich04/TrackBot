package uz.husan.signup.dto.auth;

import lombok.Data;

@Data
public class UserDTORequest {
    private String fullName;
    private String email;
    private String password;
}
