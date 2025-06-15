package uz.husan.signup.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.signup.controller.InvoiceController;
import uz.husan.signup.dto.invoice.InvoiceUpdateDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.service.InvoiceService;

@RestController
@RequiredArgsConstructor
public class InvoiceControllerImpl implements InvoiceController {
    private final InvoiceService invoiceService;
    @Override
    public ResponseEntity<?> getAllInvoices(Integer page, Integer size) {
        ResponseMessage responseMessage = invoiceService.getAllInvoices(page, size);
        return ResponseEntity.status(responseMessage.getSuccess()?200:400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> createInvoice(Long companyId,Double amount) {
        ResponseMessage responseMessage = invoiceService.createInvoice(companyId, amount);
        return ResponseEntity.status(responseMessage.getSuccess() ? 201 : 400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> updateInvoice(Long invoiceId, InvoiceUpdateDTO invoiceUpdateDTO) {
        ResponseMessage responseMessage = invoiceService.updateInvoice(invoiceId, invoiceUpdateDTO);
        return ResponseEntity.status(responseMessage.getSuccess() ? 200 : 400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> deleteInvoice(Long invoiceId) {
        ResponseMessage responseMessage = invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.status(responseMessage.getSuccess() ? 200 : 400).body(responseMessage);}
}
