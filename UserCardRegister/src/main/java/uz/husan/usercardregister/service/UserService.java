package uz.husan.usercardregister.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.husan.usercardregister.dto.ResponseMessage;
import uz.husan.usercardregister.dto.UserDTO;
import uz.husan.usercardregister.entity.Card;
import uz.husan.usercardregister.entity.Users;
import uz.husan.usercardregister.exeptions.UserBadRequestException;
import uz.husan.usercardregister.repository.CardRepository;
import uz.husan.usercardregister.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    final CardRepository cardRepository;
    public ResponseMessage getAllUsers(){
        List<Users> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseMessage.builder().status(false).data(null).message("User list is empty").build();
        }
        List<UserDTO> usersDTO = users.stream().map(user -> {
            UserDTO users1 = new UserDTO();
            users1.setId(user.getId());
            users1.setFullName(user.getFullName());
            users1.setPassword(user.getPassword());
            users1.setEmail(user.getEmail());
            users1.setBalance(user.getBalance());
            return users1;
        }).toList();
        return ResponseMessage.builder().status(true).data(usersDTO).message("user list").build();
    }

    public ResponseMessage createUser(Users users) {
        for (Users users1 : userRepository.findAll()) {
            if (users1.getEmail().equals(users.getEmail())) {
                return ResponseMessage.builder().status(false).data(null).message("This user already exists").build();
            }
        }
        return ResponseMessage.builder().status(true).data(userRepository.save(users)).message("user created").build();
    }

    public ResponseMessage updateUser(Long id, Users users) {
        if (userRepository.existsByEmailAndIdNot(users.getEmail(),id)) {
            throw new RuntimeException("User with this email already exists");
        }
        Optional<Users> users2=userRepository.findById(id);
        if (!users2.isPresent()) {
            return ResponseMessage.builder().status(false).data(null).message("User not found").build();
        }
        Users users1 = users2.get();
        users1.setFullName(users.getFullName());
        users1.setBalance(users.getBalance());
        users1.setEmail(users.getEmail());
        users1.setPassword(users.getPassword());
        return ResponseMessage.builder().status(true).data(userRepository.save(users1)).message("user succesfully updated...").build();
    }

    public ResponseMessage deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseMessage.builder().status(false).data(null).message("User not found").build();
        }
        userRepository.deleteById(id);
        return ResponseMessage.builder().status(false).data(null).message("User deleted").build();
    }

    public ResponseMessage getAllCards(Long id ) {
        return ResponseMessage.builder().status(true).data(cardRepository.findAllByCardHolderId(id)).message("cards list").build();
    }
}
