package uz.husan.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.signup.dto.transaction.TransactionRequestDTO;

@RequestMapping("/transaction")
public interface TransactionController {
    @GetMapping("/history")
    ResponseEntity<?> getAllTransactions(@RequestParam String number, @RequestParam Integer page,@RequestParam Integer size);
    @PostMapping("/pay")
    ResponseEntity<?> payTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO);
}
