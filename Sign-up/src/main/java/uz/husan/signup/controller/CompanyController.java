package uz.husan.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.husan.signup.dto.company.CompanyRequestDTO;
import uz.husan.signup.entity.Company;

@RequestMapping("/company")
public interface CompanyController {

    @PostMapping("/create")
    ResponseEntity<?> createCompany(@RequestBody CompanyRequestDTO companyRequestDTO);

    @GetMapping("/read")
    ResponseEntity<?> getCompanies(@RequestParam Integer page, @RequestParam Integer size);

    @PutMapping("/update")
    ResponseEntity<?> updateCompany(@RequestParam Long id ,@RequestBody Company company);

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCompany(@RequestParam Long companyId);

}
