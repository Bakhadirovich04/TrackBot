package uz.husan.rentcar24.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.husan.rentcar24.entity.Car;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.Status;
import uz.husan.rentcar24.entity.enums.UserRole;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarDAO {
    private final JdbcTemplate jdbcTemplate;

    public List<Car> getAllCars() {
        String sql = "SELECT * FROM cars";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Car.class));
    }
    public int updateCar(Car car) {
        String sql = """
            UPDATE cars SET 
                model = ?, 
                color = ?, 
                brand = ?, 
                year = ?, 
                description = ?, 
                price = ?, 
                rate = ?, 
                image_url = ?, 
                number = ?, 
                status = ?
            WHERE id = ?
        """;

        return jdbcTemplate.update(sql,
                car.getModel(),
                car.getColor(),
                car.getBrand(),
                car.getYear(),
                car.getDescription(),
                car.getPrice(),
                String.valueOf(car.getRate()),
                car.getImageUrl(),
                car.getNumber(),
                car.getStatus().name(),
                car.getId()
        );
    }


    public void addCar (Car car){
        String role = UserRole.USER.toString();
        String sql = "INSERT INTO cars (model, color, brand, year, description, price, rate, image_url,number) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)";
        jdbcTemplate.update(sql,
                car.getModel(),
                car.getColor(),
                car.getBrand(),
                car.getYear(),
                car.getDescription(),
                car.getPrice(),
                car.getRate().toString(),
                car.getImageUrl(),
                car.getNumber()
        );

    }

    public Optional<Car> getCarByNumber(String number) {
        String sql = "SELECT * FROM cars WHERE number = ?";
        try {
            Car car = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{number},
                    BeanPropertyRowMapper.newInstance(Car.class)
            );
            return Optional.ofNullable(car);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Optional<Car> getCarById(int idd) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try {
            Car car = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{idd},
                    BeanPropertyRowMapper.newInstance(Car.class)
            );
            return Optional.ofNullable(car);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Optional<List<User>> getAllUsersByUserRole() {
        List<User> users = jdbcTemplate.query("select * from users where role = 'USER'", BeanPropertyRowMapper.newInstance(User.class));
        return Optional.of(users);
    }
    public List<Car> getCarsByRate(String rate) {
        String sql = "SELECT * FROM cars WHERE rate = ? AND status = ?";
        return jdbcTemplate.query(
                sql,
                new Object[]{rate, Status.NOTGIVEN.name()},
                BeanPropertyRowMapper.newInstance(Car.class)
        );
    }


}
