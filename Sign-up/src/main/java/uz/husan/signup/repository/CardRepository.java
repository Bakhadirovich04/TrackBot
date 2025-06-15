package uz.husan.signup.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.husan.signup.entity.Card;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("DELETE FROM Card c WHERE c.cardNumber = :card")
    @Modifying
    @Transactional
    void deleteByNumber(@Param("card") String card);

    @Query("SELECT t FROM Card t WHERE t.cardHolder.email = :email ")
    Page<Card> findAllByCardsEm(@Param("email") String email, Pageable pageable);

  Optional<Card> findCardByCardNumber(String cardNumber);

    Boolean existsByCardNumber(String cardNumber);


    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.password = :new_pin, c.balance = :balance12,c.updateDate=:time1 WHERE c.cardNumber = :cardNumber")
    void updateCard(@Param("cardNumber") String cardNumber,
                    @Param("new_pin") String newPin,
                    @Param("balance12") Double balance12,
                    @Param("time1") LocalDateTime time1);



}
