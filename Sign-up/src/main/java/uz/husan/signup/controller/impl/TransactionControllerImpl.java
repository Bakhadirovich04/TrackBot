package uz.husan.signup.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.signup.controller.TransactionController;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.dto.transaction.TransactionRequestDTO;
import uz.husan.signup.service.TransactionService;

@RestController
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {
    private final TransactionService transactionService;
    @Override
    public ResponseEntity<?> getAllTransactions(String number, Integer page, Integer size) {
        ResponseMessage responseMessage = transactionService.getAllTransactions(number,page, size);
        return ResponseEntity.status(responseMessage.getSuccess()?200:400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> payTransaction(TransactionRequestDTO transactionRequestDTO) {
        ResponseMessage responseMessage = transactionService.payTransaction(transactionRequestDTO);
        return ResponseEntity.status(responseMessage.getSuccess()?200:400).body(responseMessage);
    }
}
