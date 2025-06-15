package uz.app.parking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.app.parking.entity.Car;
import uz.app.parking.entity.Invoice;
import uz.app.parking.repository.CarRepository;
import uz.app.parking.repository.InvoiceRepository;

;import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    private final InvoiceRepository invoiceRepository;
    private final CarRepository carRepository;

    public InvoiceController(InvoiceRepository invoiceRepository, CarRepository carRepository) {
        this.invoiceRepository = invoiceRepository;
        this.carRepository = carRepository;
    }

    @GetMapping
    public List<Invoice> getInvoices(@RequestParam Long id) {
        Optional<Car> carbyId = carRepository.findById(id);
        if (carbyId.isPresent()) {
            Car car = carbyId.get();
            return invoiceRepository.findAllByCar(car);
        }
        return null;
    }
}
