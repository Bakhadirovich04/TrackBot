package uz.husan.rentcar24.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.husan.rentcar24.controller.BookingController;
import uz.husan.rentcar24.entity.Booking;
import uz.husan.rentcar24.entity.Car;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingDAO {
    private final JdbcTemplate jdbcTemplate;
    public void addBooking(Booking booking) {
        String sql = """
        INSERT INTO booking 
            (user_id, car_id, start_date, end_date, price, status) 
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        jdbcTemplate.update(
                sql,
                // 1) user_id — integer
                booking.getUser().getId(),
                // 2) car_id — integer
                booking.getCar().getId(),
                // 3) start_date — timestamp
                java.sql.Timestamp.valueOf(booking.getStartDate()),
                // 4) end_date — timestamp
                java.sql.Timestamp.valueOf(booking.getEndDate()),
                // 5) price — double
                booking.getPrice(),
                // 6) status — String (ENUM name)
                booking.getStatus().name()
        );
    }

    public List<Booking> getAllBookingsByUser(User user) {

        String sql = """
            SELECT
                b.id            AS booking_id,
                b.start_date    AS start_date,
                b.end_date      AS end_date,
                b.price         AS price,
                b.status        AS status,
                c.id            AS car_id,
                c.model         AS model,
                c.image_url     AS image_url
            FROM booking b
            JOIN cars c ON b.car_id = c.id
            WHERE b.user_id = ? and b.status = 'GIVEN'
        """;

        return jdbcTemplate.query(sql, new Object[]{ user.getId() }, (rs, rowNum) -> {
            Booking booking = new Booking();
            booking.setId(rs.getInt("booking_id"));
            booking.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
            booking.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
            booking.setPrice(rs.getDouble("price"));
            booking.setStatus(Status.valueOf(rs.getString("status")));

            Car car = new Car();
            car.setId(rs.getInt("car_id"));
            car.setModel(rs.getString("model"));
            car.setImageUrl(rs.getString("image_url"));
            booking.setCar(car);

            booking.setUser(user);
            return booking;
        });
    }

    public List<Booking> getAllBooking() {
        String sql = "SELECT * FROM booking";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Booking.class));
    }
}
