package uz.app.parking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
    private LocalDateTime enterTime;
    private LocalDateTime exitTime;
    private Boolean active;
    private Double price;

}
