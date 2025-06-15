package uz.husan.signup.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.husan.signup.dto.auth.EmailAndPasswordDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.dto.auth.UserDTORequest;
import uz.husan.signup.entity.User;
import uz.husan.signup.entity.enums.Role;
import uz.husan.signup.repository.UserRepository;
import uz.husan.signup.service.UserService;

import java.util.Base64;
import java.util.Random;

@RequiredArgsConstructor
@Service("UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
     final JWTService jwtService;
     final PasswordEncoder passwordEncoder;
    @Override
    public ResponseMessage confirm(String code, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        boolean b = userRepository.existsByEmailAndConfCode(email, code);
            if (b) {
                if (user.getEnabled()) {
                    return ResponseMessage.builder()
                            .success(false)
                            .message("Email is already confirmed")
                            .data("00i8hut348r403t-9ru2fc2ew0ce2djc==")
                            .build();
                }
                user.setEnabled(true);
                userRepository.save(user);
                return ResponseMessage.builder()
                        .success(true)
                        .message("Email confirmed successfully")
                        .data(user.getEmail())
                        .build();
            }
        return ResponseMessage.builder()
                .success(false)
                .message("CONFIRMATION CODE OR EMAIL IS INCORRECT")
                .data(email+"  \n " + code)
                .build();
    }

    @Override
    public ResponseMessage signup(UserDTORequest userDTORequest) {
        if (userRepository.existsByEmail(userDTORequest.getEmail())) {
            return ResponseMessage.builder()
                        .success(false)
                        .message("User already exists")
                        .data("00i8hut348r403t-9ru2fc2ew0ce2djc==")
                        .build();
        }
         User user = new User();
        user.setFullName(userDTORequest.getFullName());
        user.setEmail(userDTORequest.getEmail());
        String s = new String(Base64.getEncoder().encode(userDTORequest.getEmail().getBytes()));
        user.setPassword(passwordEncoder.encode(userDTORequest.getPassword()))  ;
        user.setEnabled(false);
        user.setRole(Role.USER);
        user.setBalance(00.00);
        String code = generateCode();
        user.setConfCode(code);
        userRepository.save(user);
        sendVerificationCode(userDTORequest.getEmail(),code);
        return ResponseMessage.builder().success(true).message("sign up done..").data(s).build();

    }


    public void sendVerificationCode(String toEmail,String code) {
        String subject = "Tasdiqlash kodingiz";
        String body = "Hurmatli foydalanuvchi,\n\nSizning tasdiqlash kodingiz: " + code + "\n\nIltimos, bu kodni hech kimga bermang. Yoki quyidagi link orqali tasdiqlang:\n" +
                "http://localhost:8081/auth/confirm?code=" + code + "&email=" + toEmail + "\n\n" +
                "Hurmat bilan,\n" +
                "Sizning tizimingiz;";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("husanchoriyev20040406@gmail.com"); // o'z emailingiz
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public static String generateCode(){
        StringBuilder confCode = new StringBuilder();
            Random random = new Random();
            for (int j = 0; j < 6; j++) {
                confCode.append(random.nextInt(9));
            }
            return confCode.toString();
    }
    @Override
    public ResponseMessage signin(EmailAndPasswordDTO emailAndPasswordDTO) {
        User user = userRepository.findByEmail(emailAndPasswordDTO.getEmail()).orElseThrow();
        if (passwordEncoder.matches(emailAndPasswordDTO.getPassword(), user.getPassword())) {
            if (!user.getEnabled()) {
                return ResponseMessage.builder()
                        .data(emailAndPasswordDTO.getEmail())
                        .success(false)
                        .message("Please confirm your email")
                        .build();
            }
/*
            String s = new String(Base64.getEncoder().encode(user.getEmail().getBytes()));
*/
            String s = jwtService.generateToken(user.getUsername());
            return ResponseMessage.builder()
                    .data(user)
                    .success(true)
                    .message("Sign in successful")
                    .data(s)
                    .build();
        }
        return ResponseMessage.builder().data(emailAndPasswordDTO).message("email or password incorrect").success(false).build();
    }

}
