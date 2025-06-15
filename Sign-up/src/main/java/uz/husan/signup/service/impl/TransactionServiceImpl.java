package uz.husan.signup.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.husan.signup.dto.card.CardResponseDTO;
import uz.husan.signup.dto.transaction.TransactionResponseDTO;
import uz.husan.signup.entity.*;
import uz.husan.signup.dto.transaction.TransactionRequestDTO;
import uz.husan.signup.entity.enums.Status;
import uz.husan.signup.entity.enums.TRStatus;
import uz.husan.signup.repository.*;
import uz.husan.signup.service.TransactionService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public ResponseMessage getAllTransactions(String number,Integer page, Integer size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<TransactionResponseDTO> all =transactionRepository
                .findAllByUser(user, pageRequest).map(this::transactionToDTO);
        if (all.isEmpty()){
            return ResponseMessage.builder().success(false).message("Transactions no such exists").data(null).build();
        }
        return ResponseMessage
                .builder()
                .message("Transactions fetched successfully")
                .success(true)
                .data(all)
                .build();
    }
    public TransactionResponseDTO transactionToDTO(Transactions transactions) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setFromCardNumber(transactions.getFromCardNumber());
        responseDTO.setInvoiceNumber(transactions.getInvoiceNumber());
        responseDTO.setAmount(transactions.getAmount());
        responseDTO.setCompanyName(transactions.getCompany().getCompanyName());
        responseDTO.setCardHolderName(transactions.getFrom_user().getFullName());
        responseDTO.setTransactionDate(transactions.getTransactionDate());
        return responseDTO;
    }

    @Override
    public ResponseMessage payTransaction(TransactionRequestDTO transactionRequestDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Card> fromCard = cardRepository.findCardByCardNumber(transactionRequestDTO.getFromCardNumber());
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumber(transactionRequestDTO.getInvoiceNumber());
        Card fromCards = fromCard.get();
        if(!fromCards.getCardHolder().getEmail().equals(user.getEmail())) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("The card is not yours, so you cannot make the transfer.")
                    .data(null)
                    .build();
        }
        if (!invoice.isPresent()){
            return ResponseMessage.builder()
                    .success(false)
                    .message("The Invoice you are trying to transfer does not exist.")
                    .data(null)
                    .build();

        }
        if(invoice.get().getStatus().equals(Status.CONFIRMED)){
            return ResponseMessage.builder()
                    .success(false)
                    .message("A payment has already been made with this Invoice Number.")
                    .data(null)
                    .build();
        }
        if(transactionRequestDTO.getFromCardNumber().isEmpty() || transactionRequestDTO.getInvoiceNumber().isEmpty()) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("Card number or invoice number cannot be empty")
                    .data(null)
                    .build();
        }
        if(!transactionRequestDTO.getAmount().equals(invoice.get().getAmount())) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("Please enter the same value as given on the Invoice...!!!")
                    .data(null)
                    .build();
        }
        if(user==null) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("User not authenticated")
                    .data(null)
                    .build();
        }
        if (!fromCard.isPresent()) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("Card not found")
                    .data(null)
                    .build();
        }

        Invoice invoice1 = invoice.get();
        if( transactionRequestDTO.getAmount() <= 0&& transactionRequestDTO.getAmount() > fromCards.getBalance()) {
            return ResponseMessage.builder()
                    .success(false)
                    .message("TRANSFER CANNOT BE PERFORMED OR YOUR BALANCE IS NOT ENOUGH")
                    .data(null)
                    .build();
        }
        fromCards.setBalance(fromCards.getBalance() - (transactionRequestDTO.getAmount()));
        fromCards.getCardHolder().setBalance(fromCards.getCardHolder().getBalance() - transactionRequestDTO.getAmount());
        userRepository.save(fromCards.getCardHolder());
        invoice1.getCompany().setBalance(invoice1.getCompany().getBalance() + transactionRequestDTO.getAmount());
        invoice1.setStatus(Status.CONFIRMED);
        companyRepository.save(invoice1.getCompany());
        invoiceRepository.save(invoice1);
        cardRepository.save(fromCards);
        Transactions transactions = new Transactions();
        transactions.setFromCardNumber(transactionRequestDTO.getFromCardNumber());
        transactions.setInvoiceNumber(transactionRequestDTO.getInvoiceNumber());
        transactions.setAmount(transactionRequestDTO.getAmount());
        transactions.setCompany(invoice1.getCompany());
        transactions.setFrom_user(fromCards.getCardHolder());
        transactions.setStatus(TRStatus.ACTIVE);
        transactions.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transactions);
        return ResponseMessage.builder()
                .success(true)
                .message("Transaction completed successfully")
                .data(transactions.getAmount())
                .build();
    }
}
