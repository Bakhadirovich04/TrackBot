package uz.husan.usercardregister.dto;

import lombok.Data;
import uz.husan.usercardregister.entity.Card;

import java.util.List;
@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private Double balance;
}
