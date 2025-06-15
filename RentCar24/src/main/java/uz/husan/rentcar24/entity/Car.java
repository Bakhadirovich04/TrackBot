package uz.husan.rentcar24.entity;

import lombok.Data;
import uz.husan.rentcar24.entity.enums.CarRate;
import uz.husan.rentcar24.entity.enums.Status;
@Data
public class Car {
    private int id;
    private String model;
    private String color;
    private String brand;
    private int year;
    private String description;
    private Double price;
    private CarRate rate;
    private String imageUrl;
    private String number;
    private Status status;

}
