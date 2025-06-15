package uz.husan.rentcar24.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Lombok;
import uz.husan.rentcar24.entity.enums.Status;

import java.time.LocalDateTime;
@Data
public class BookingDTO {
    private int id;
    private String image;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status carStatus;
    private String price;
}
