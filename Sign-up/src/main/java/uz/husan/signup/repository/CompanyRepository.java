package uz.husan.signup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.husan.signup.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyName(String companyName);

    boolean existsCompaniesById(Long id);

    boolean existsByCompanyNameAndAddress(String companyName, String address);
}
