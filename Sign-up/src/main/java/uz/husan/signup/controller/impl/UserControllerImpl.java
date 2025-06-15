package uz.husan.signup.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.signup.controller.UserController;
import uz.husan.signup.dto.auth.EmailAndPasswordDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.dto.auth.UserDTORequest;
import uz.husan.signup.service.UserService;
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;
    @Override
    public ResponseEntity<?> signup(UserDTORequest userDTORequest) {
        ResponseMessage signupResponse = userService.signup(userDTORequest);
       return ResponseEntity.status(signupResponse.getSuccess()?200:400).body(signupResponse);
    }

    @Override
    public ResponseEntity<?> signin(EmailAndPasswordDTO emailAndPasswordDTO) {
        ResponseMessage signin = userService.signin(emailAndPasswordDTO);
        return ResponseEntity.status(signin.getSuccess()?200:400).body(signin);
    }

    @Override
    public ResponseEntity<?> confirm(String code, String email) {
        ResponseMessage confirmResponse = userService.confirm(code,email);
        return ResponseEntity.status(confirmResponse.getSuccess()?200:400).body(confirmResponse);
    }
}
