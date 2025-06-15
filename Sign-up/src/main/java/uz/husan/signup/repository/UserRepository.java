package uz.husan.signup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.husan.signup.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
    @Query("SELECT u FROM User u WHERE u.email = :email ")
    Optional<User> findByEmail(String email);

    List<User> findAllByRole(String role);

    boolean existsByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);

    boolean existsByEmailAndConfCode(String email, String confCode);
}
