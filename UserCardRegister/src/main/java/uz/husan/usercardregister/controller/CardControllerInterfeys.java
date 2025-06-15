package uz.husan.usercardregister.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.usercardregister.entity.Card;

@RequestMapping("/card")
public interface CardControllerInterfeys {
    @GetMapping
    ResponseEntity<?> getAllCards();
    @PostMapping("/create")
    ResponseEntity<?> createCard(@RequestParam Long id,@RequestBody Card card);
    @PutMapping("/update")
    ResponseEntity<?> updateCard(@RequestParam Long id, @RequestParam(required = false) Long userId, @RequestBody Card card);
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCard(@PathVariable Long id);
}
