package uz.husan.rentcar24.entity;

import lombok.Data;
import uz.husan.rentcar24.entity.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Booking {
    private int id;
    private  User user;
    private Car car;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double price;
    private Status status;
}
