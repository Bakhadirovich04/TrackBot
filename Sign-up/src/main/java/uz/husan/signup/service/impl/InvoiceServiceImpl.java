package uz.husan.signup.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.husan.signup.dto.card.CardResponseDTO;
import uz.husan.signup.dto.invoice.InvoiceResponseDTO;
import uz.husan.signup.dto.invoice.InvoiceUpdateDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.entity.Company;
import uz.husan.signup.entity.Invoice;
import uz.husan.signup.entity.enums.Status;
import uz.husan.signup.repository.CompanyRepository;
import uz.husan.signup.repository.InvoiceRepository;
import uz.husan.signup.service.InvoiceService;

import java.time.LocalDateTime;
import java.util.Optional;
@RequiredArgsConstructor
@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {
    private final CompanyRepository companyRepository;
    private final InvoiceRepository invoiceRepository;
    @Override
    public ResponseMessage getAllInvoices(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        Page<InvoiceResponseDTO> all =invoiceRepository
                .findAll(pageRequest).map(this::getInvoices);
        if (all.isEmpty()){
            return ResponseMessage.builder().success(false).message("Invoice no such exists").data(null).build();
        }
        return ResponseMessage.builder().data(all).message("Invoices fetched successfully").success(true).build();
    }
    public InvoiceResponseDTO getInvoices(Invoice invoice) {
        InvoiceResponseDTO responseDTO = new InvoiceResponseDTO();
        responseDTO.setId(invoice.getId());
        responseDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        responseDTO.setCompanyName(invoice.getCompany().getCompanyName());
        responseDTO.setAmount(invoice.getAmount());
        responseDTO.setStatus(invoice.getStatus().name());
        return responseDTO;
    }

    @Override
    public ResponseMessage createInvoice(Long companyId, Double amount) {
        Optional<Company> byId = companyRepository.findById(companyId);
        if (!byId.isPresent()) {
            return ResponseMessage.builder().success(false).message("Company not found").data(null).build();
        }
        Company company = byId.get();
        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        String invoiceNumber = UserServiceImpl.generateCode();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setAmount(amount);
        invoice.setStatus(Status.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
        InvoiceResponseDTO invoiceResponseDTO =getInvoices(invoice);
        return ResponseMessage.builder().success(true).message("Invoice created successfully").data(invoiceResponseDTO).build();
    }

    @Override
    public ResponseMessage updateInvoice(Long invoiceId, InvoiceUpdateDTO invoiceUpdateDTO) {
        Optional<Invoice> byId = invoiceRepository.findById(invoiceId);
        if (!byId.isPresent()){
            return ResponseMessage.builder().success(false).message("Invoice not found").data(null).build();
        }
        Invoice invoice = byId.get();
        invoice.setAmount(invoiceUpdateDTO.getAmount());
        if (invoiceUpdateDTO.getStatus().equals("PENDING")) {
            invoice.setStatus(Status.PENDING);
        }
        else if (invoiceUpdateDTO.getStatus().equals("REJECTED")) {
            invoice.setStatus(Status.REJECTED);
        }
        else if (invoiceUpdateDTO.getStatus().equals("CONFIRMED")) {
            invoice.setStatus(Status.CONFIRMED);
        } else {
            return ResponseMessage.builder().success(false).message("Invalid status").data(null).build();
        }
        invoiceRepository.save(invoice);
        InvoiceResponseDTO invoiceResponseDTO = getInvoices(invoice);
        return ResponseMessage.builder().success(true).message("Invoice updated successfully").data(invoiceResponseDTO).build();
    }

    @Override
    public ResponseMessage deleteInvoice(Long invoiceId) {
       boolean exists = invoiceRepository.existsById(invoiceId);
        if (!exists) {
            return ResponseMessage.builder().success(false).message("Invoice not found").data(null).build();
        }
        invoiceRepository.deleteById(invoiceId);
        return ResponseMessage.builder().success(true).message("Invoice deleted successfully").data(null).build();
    }
}
