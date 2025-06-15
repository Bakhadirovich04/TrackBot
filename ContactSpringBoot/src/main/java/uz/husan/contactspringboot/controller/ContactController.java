package uz.husan.contactspringboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.contactspringboot.repository.ContactRepository;
import uz.husan.contactspringboot.entity.Contact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactRepository contactRepository;
    @PostMapping
    public Contact create(@RequestBody Contact contact){
        Boolean exists = false;
        for (Contact contact1 : contactRepository.findAll()) {
            if (contact1.getNumber().equals(contact.getNumber())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            contact.setCreatedate(LocalDateTime.now());
            contactRepository.save(contact);
            return contact;
        }
        else return null;
    }

    @PutMapping("/update")
    public String  update(@RequestBody Contact contact,@RequestParam Long id) {
        Optional<Contact> contact1 = contactRepository.findById(id);
        if (contact1.isPresent()) {
            if (contact.getNumber().equals(contact1.get().getNumber())) {
                Contact contact2 = contact1.get();
                contact2.setNumber(contact.getNumber());
                contact2.setCreatedate(LocalDateTime.now());
                contact2.setName(contact.getName());
                contactRepository.save(contact2);
                return "muvaffaqiyatli o'zgartirildi";
            }
        } else {
            for (Contact contact2 : contactRepository.findAll()) {
                if (contact.getNumber().equals(contact2.getNumber())) {
                    return "afsuski o'zgartirilmadi";
                }
            }
            if (contact1.isPresent()) {
                Contact contact2 = contact1.get();
                contact2.setNumber(contact.getNumber());
                contact2.setCreatedate(LocalDateTime.now());
                contact2.setName(contact.getName());
                contactRepository.save(contact2);
                return "muvaffaqiyatli o'zgartirildi";
            }

        }
        return "afsuski o'zgartirilmadi";
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteContact(@RequestParam Long id) {
        return contactRepository.findById(id)
                .map(contact -> {
                    contactRepository.deleteById(id);
                    return ResponseEntity.ok("Contact deleted successfully.");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Contact not found."));
    }
    @GetMapping
    public List<Contact> getContacts() {
        return contactRepository.findAll();
    }
}
