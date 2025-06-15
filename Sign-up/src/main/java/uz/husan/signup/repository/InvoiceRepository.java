package uz.husan.signup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.husan.signup.entity.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsInvoiceByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
