package uz.husan.signup.repository;

import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.husan.signup.entity.Transactions;
import uz.husan.signup.entity.User;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    @Query("SELECT t FROM Transactions t WHERE t.from_user = :user")
    Page<Transactions> findAllByUser(@Param("user") User user, Pageable pageable);

}
