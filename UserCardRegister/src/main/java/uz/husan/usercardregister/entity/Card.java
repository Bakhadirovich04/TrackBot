package uz.husan.usercardregister.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Card extends AbsEntity {
    private String cardNumber;
    @ManyToOne
    @JsonBackReference
    private Users cardHolder;
    private Double balance;
    private String cvv;
    private String cardName;
    @CreationTimestamp
    private LocalDateTime registerDate;
    @LastModifiedDate
    private LocalDateTime updateDate;

}
