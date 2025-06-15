package uz.husan.contactspringboot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String number;
    private String name;
    private LocalDateTime createdate;
}
