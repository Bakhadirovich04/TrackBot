package uz.husan.usercardregister.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.usercardregister.entity.Users;

@RequestMapping("/user")
public interface UserControllerInterfeys {
    @GetMapping
    ResponseEntity<?> getAllUsers();
    @GetMapping("/{id}/cards")
    ResponseEntity<?> getAllCards(@PathVariable Long id);
    @PostMapping
    ResponseEntity<?> createUser(@RequestBody Users users);
    @PutMapping({"/update"})
    ResponseEntity<?> updateUser(@RequestParam Long id, @RequestBody Users users);
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id);

}
