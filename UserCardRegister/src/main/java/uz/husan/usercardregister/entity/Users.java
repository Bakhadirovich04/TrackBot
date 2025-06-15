package uz.husan.usercardregister.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Users extends AbsEntity {
    private String fullName;
    private String email;
    private String password;
    private Double balance;
    @OneToMany(mappedBy = "cardHolder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Card> card;
}
