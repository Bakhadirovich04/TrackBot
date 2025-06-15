package uz.husan.signup.service;

import org.springframework.stereotype.Service;
import uz.husan.signup.dto.invoice.InvoiceUpdateDTO;
import uz.husan.signup.entity.ResponseMessage;

@Service
public interface InvoiceService {
   ResponseMessage getAllInvoices( Integer page,  Integer size);

   ResponseMessage createInvoice( Long companyId,Double amount);

   ResponseMessage updateInvoice( Long invoiceId,InvoiceUpdateDTO invoiceUpdateDTO);

   ResponseMessage deleteInvoice( Long invoiceId);
}
