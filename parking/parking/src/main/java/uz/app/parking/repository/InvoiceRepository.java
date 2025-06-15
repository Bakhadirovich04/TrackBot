package uz.app.parking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.parking.entity.Car;
import uz.app.parking.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findInvoicesByCar(Car car);

    Optional<Invoice> findByCarAndActive(Car car, boolean active);

    List<Invoice> findAllByCar(Car car);
}
