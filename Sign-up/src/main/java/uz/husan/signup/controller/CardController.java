package uz.husan.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.signup.dto.card.CardRequestDTO;


@RequestMapping("/card")
public interface CardController {

    @GetMapping("/read")
    ResponseEntity<?> getAllCards(@RequestParam Integer page, @RequestParam Integer size);

    @PostMapping("/create")
    ResponseEntity<?> createCard(@RequestBody CardRequestDTO cardRequestDTO);

    @PutMapping("/update")
    ResponseEntity<?> updateCard(@RequestParam String card_number,  @RequestParam String last_pin, @RequestParam String new_pin,@RequestParam Double balance);

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCard(@RequestParam String card_number);
}
