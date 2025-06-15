package uz.husan.signup.service;

import org.springframework.stereotype.Service;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.dto.transaction.TransactionRequestDTO;

@Service
public interface TransactionService {

   ResponseMessage getAllTransactions(String number, Integer page, Integer size);

   ResponseMessage payTransaction(TransactionRequestDTO transactionRequestDTO);
}
