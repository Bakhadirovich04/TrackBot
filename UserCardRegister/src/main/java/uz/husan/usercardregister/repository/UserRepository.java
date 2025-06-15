package uz.husan.usercardregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.husan.usercardregister.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
}
