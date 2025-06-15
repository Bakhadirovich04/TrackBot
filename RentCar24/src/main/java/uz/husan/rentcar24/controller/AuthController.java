package uz.husan.rentcar24.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.husan.rentcar24.dao.UserDAO;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.UserRole;

import java.util.Optional;
@Controller
@Component
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/signin")
    public String signin(Model model) {return "signin";}
    @GetMapping("/signup")
    public String signup(Model model) {return "signup";}

    @PostMapping("/signup")
    public String signup(@RequestParam("fullname") String fullname,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam("balance") String balance,
                         Model model) {
        Optional<User> optionalUser = Optional.ofNullable(userDAO.getUserByEmailAndPassword(email, password));
        if (optionalUser.isPresent()){
            model.addAttribute("message", "user already signed");
            return "autherror";
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setFullName(fullname);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setBalance(Double.parseDouble(balance));
        user.setRole(UserRole.USER);

        userDAO.addUser(user);
        return "redirect:/signin";
    }

}
