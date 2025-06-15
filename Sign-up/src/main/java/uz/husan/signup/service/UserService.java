package uz.husan.signup.service;

import org.springframework.stereotype.Service;
import uz.husan.signup.dto.auth.EmailAndPasswordDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.dto.auth.UserDTORequest;

@Service
public interface UserService {
    ResponseMessage signup(UserDTORequest userDTORequest);
    ResponseMessage signin(EmailAndPasswordDTO emailAndPasswordDTO);
    ResponseMessage confirm(String code, String email);
}
