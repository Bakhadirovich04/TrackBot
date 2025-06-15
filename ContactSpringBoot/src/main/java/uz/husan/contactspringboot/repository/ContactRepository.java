package uz.husan.contactspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.husan.contactspringboot.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact,Long> {

}
