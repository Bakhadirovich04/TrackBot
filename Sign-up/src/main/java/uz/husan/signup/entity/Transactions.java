package uz.husan.signup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import uz.husan.signup.entity.enums.TRStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transactions extends AbsEntity{
    private String fromCardNumber;
    private String invoiceNumber;
    private Double amount;
    @ManyToOne
    private Company company;
    @ManyToOne
    private User from_user;
    @Enumerated(EnumType.STRING)
    private TRStatus status;
    @CreationTimestamp
    private LocalDateTime transactionDate;

}
