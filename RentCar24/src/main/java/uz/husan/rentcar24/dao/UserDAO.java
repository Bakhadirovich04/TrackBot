package uz.husan.rentcar24.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.UserRole;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDAO    {
    private final JdbcTemplate jdbcTemplate;

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public User getUserByEmailAndPassword (String email, String password){
        try {
            return jdbcTemplate.queryForObject(
                    "select * from users where email = ? and password = ?", new BeanPropertyRowMapper<>(User.class),
                    email, password
            );

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addUser (User user){
        String sql = "INSERT INTO users (email,password,fullname,balance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getFullName(),user.getBalance());
    }

    public Optional<User> getUserByEmail(String email){
        List<User> users = jdbcTemplate.query("select * from users where email = ?", BeanPropertyRowMapper.newInstance(User.class), email);
        if (users.size() != 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    public Optional<List<User>> getAllUsersByUserRole() {
        List<User> users = jdbcTemplate.query("select * from users where role = 'USER'", BeanPropertyRowMapper.newInstance(User.class));
        return Optional.of(users);
    }


        public int updateUser(User user) {
            String sql = """
            UPDATE users
            SET email      = ?,
                password   = ?,
                balance    = ?,
                fullname  = ?,
                role       = ?
            WHERE id = ?
        """;

            return jdbcTemplate.update(
                    sql,
                    user.getEmail(),
                    user.getPassword(),
                    user.getBalance(),
                    user.getFullName(),
                    user.getRole().name(),  // if role is an enum
                    user.getId()
            );
        }


    public User getUserById(int id) {
        String sql = "SELECT id, email, password, balance, fullname, role FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    id
            );
        } catch (EmptyResultDataAccessException ex) {
            // Agar bunday foydalanuvchi topilmasa, null yoki custom exception qaytarishingiz mumkin
            return null;
        }
    }

    public User getUserByRole(String role) {
        String sql = "SELECT * FROM users WHERE role = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{role},
                    BeanPropertyRowMapper.newInstance(User.class)
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // Agar topilmasa null qaytariladi
        }
    }
}
