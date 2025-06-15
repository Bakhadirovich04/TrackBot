package uz.husan.usercardregister.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.usercardregister.controller.UserControllerInterfeys;
import uz.husan.usercardregister.dto.ResponseMessage;
import uz.husan.usercardregister.entity.Users;
import uz.husan.usercardregister.repository.CardRepository;
import uz.husan.usercardregister.repository.UserRepository;
import uz.husan.usercardregister.service.UserService;
@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerInterfeys{
    final UserService userService;
    @Override
    public ResponseEntity<?> createUser(Users users) {
        return ResponseEntity.ok(userService.createUser(users));
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<?> getAllCards(Long id) {
        return ResponseEntity.ok(userService.getAllCards(id));
    }

    @Override
    public ResponseEntity<?> updateUser(Long id, Users users) {
        return ResponseEntity.ok(userService.updateUser(id,users));
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
