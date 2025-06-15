package uz.husan.usercardregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.husan.usercardregister.entity.Card;
import uz.husan.usercardregister.entity.Users;

import java.util.List;

public interface CardRepository extends JpaRepository< Card, Long > {
   List< Card > findAllByCardHolderId(Long id );

   boolean existsByCardNumberAndIdNot(String cardNumber, Long id);
}
