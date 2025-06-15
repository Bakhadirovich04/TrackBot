package uz.husan.signup.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Card extends AbsEntity{
    private String cardNumber;
    private String password;
    @ManyToOne
    @JsonBackReference
    private User cardHolder;
    private Double balance;
    private String cvv;
    private String expiryDate;
    private String cardType;
    @CreationTimestamp
    private LocalDateTime registerDate;
    @LastModifiedDate
    private LocalDateTime updateDate;
}
