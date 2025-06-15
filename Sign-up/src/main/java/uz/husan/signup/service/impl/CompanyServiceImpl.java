package uz.husan.signup.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.husan.signup.dto.company.CompanyRequestDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.entity.Company;
import uz.husan.signup.repository.CompanyRepository;
import uz.husan.signup.service.CompanyService;

import java.time.LocalDateTime;

@Service("companyService")
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    @Override
    public ResponseMessage createCompany(CompanyRequestDTO companyRequestDTO) {
        boolean b = companyRepository.existsByCompanyName(companyRequestDTO.getCompanyName());
        if(b) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("This company already exists")
                    .build();
        }
        Company newCompany = new Company();
        newCompany.setCompanyName(companyRequestDTO.getCompanyName());
        newCompany.setAddress(companyRequestDTO.getAddress());
        newCompany.setPhoneNumber(companyRequestDTO.getPhoneNumber());
        newCompany.setBalance(companyRequestDTO.getBalance());
        newCompany.setCreatedAt(LocalDateTime.now());
                companyRepository.save(newCompany);
        return ResponseMessage.builder()
                .success(true)
                .data(newCompany)
                .message("Company successfully created")
                .build();
    }

    @Override
    public ResponseMessage getCompanies(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        Page<Company> all = companyRepository
                .findAll(pageRequest);
        if (all.isEmpty()){
            return ResponseMessage.builder().success(false).message("Cards no such exists").data(null).build();
        }
        return ResponseMessage.builder().data(all).message("Companies fetched successfully").success(true).build();
    }

    @Override
    public ResponseMessage updateCompany(Long id,Company company) {
        boolean b = companyRepository.existsCompaniesById(id);
        if(!b) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("This company does not exist")
                    .build();
        }
        Company existingCompany = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
        existingCompany.setCompanyName(company.getCompanyName());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setPhoneNumber(company.getPhoneNumber());
        existingCompany.setBalance(company.getBalance());
        Company updatedCompany = companyRepository.save(existingCompany);
        return ResponseMessage.builder()
                .success(true)
                .data(updatedCompany)
                .message("Company successfully updated")
                .build();
    }

    @Override
    public ResponseMessage deleteCompany(Long companyId) {
        boolean b = companyRepository.existsCompaniesById(companyId);
        if(!b) {
            return ResponseMessage.builder()
                    .success(false)
                    .data(null)
                    .message("This company does not exist")
                    .build();
        }
        companyRepository.deleteById(companyId);
        return ResponseMessage.builder()
                .success(true)
                .data(null)
                .message("Company successfully deleted")
                .build();
    }
}
