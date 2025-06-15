package uz.husan.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.signup.dto.auth.EmailAndPasswordDTO;
import uz.husan.signup.dto.auth.UserDTORequest;

@RequestMapping("/auth")
public interface UserController {
     @PostMapping("/signup")
     ResponseEntity<?> signup(@RequestBody UserDTORequest userDTORequest);

     @PostMapping("/signin")
     ResponseEntity<?> signin(@RequestBody EmailAndPasswordDTO emailAndPasswordDTO);

     @GetMapping("/confirm")
     ResponseEntity<?> confirm(@RequestParam String code,@RequestParam String email);

}
