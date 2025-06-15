package uz.husan.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.signup.dto.invoice.InvoiceUpdateDTO;

@RequestMapping("/invoice")
public interface InvoiceController {

  @GetMapping("/read")
  ResponseEntity<?> getAllInvoices(@RequestParam Integer page, @RequestParam Integer size);

  @GetMapping("/create")
  ResponseEntity<?> createInvoice(@RequestParam Long companyId,@RequestParam Double amount);

  @PutMapping("/update")
  ResponseEntity<?> updateInvoice(@RequestParam Long invoiceId, @RequestBody InvoiceUpdateDTO invoiceUpdateDTO);

  @DeleteMapping("/delete")
  ResponseEntity<?> deleteInvoice(@RequestParam Long invoiceId);
}
